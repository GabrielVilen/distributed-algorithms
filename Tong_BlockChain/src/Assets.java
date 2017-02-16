import java.util.*;

import com.chain.api.*;
import com.chain.http.*;
import com.chain.signing.*;

class Assets {
  public static void main(String[] args) throws Exception {
  	
  	//创建一个叫Client的客户端
    Client client = new Client();
     
     
    //创建一个叫做assetkey的key,并装载到HsmSigner 
    MockHsm.Key assetKey = MockHsm.Key.create(client);
    HsmSigner.addKey(assetKey, MockHsm.getSignerClient(client));

    //创建一个叫做accountKey的key,并装载到HsmSigner 
    MockHsm.Key accountKey = MockHsm.Key.create(client);
    HsmSigner.addKey(accountKey, MockHsm.getSignerClient(client));


    //创建一个新的账号，叫acme_treasury(金库)，添加accountKey(key)
    //设置Quorum为1
    new Account.Builder()
      .setAlias("acme_treasury")
      .addRootXpub(accountKey.xpub)
      .setQuorum(1)
      .create(client);

    // snippet create-asset-acme-common
    // 创建一个新的Asset,名字叫acme_common，使用的Key是assetKey
    // Quorum是1
    // 定义了几个Definition和几个Tag
    new Asset.Builder()
      .setAlias("acme_common")
      .addRootXpub(assetKey.xpub)
      .setQuorum(1)
      .addTag("internal_rating", "1")
      .addDefinitionField("issuer", "Acme Inc.")
      .addDefinitionField("type", "security")
      .addDefinitionField("subtype", "private")
      .addDefinitionField("class", "common")
      .create(client);
    // endsnippet

    // snippet create-asset-acme-preferred
    // 创建一个新的Asset,名字叫acme_preferred，使用的Key是assetKey
    // Quorum是1
    // 定义了几个Definition和几个Tag
    new Asset.Builder()
      .setAlias("acme_preferred")
      .addRootXpub(assetKey.xpub)
      .setQuorum(1)
      .addTag("internal_rating", "2")
      .addDefinitionField("issuer", "Acme Inc.")
      .addDefinitionField("type", "security")
      .addDefinitionField("subtype", "private")
      .addDefinitionField("class", "preferred")
      .create(client);
    // endsnippet

    // snippet list-local-assets
    // 创建一个新的查询叫localAssets
    // 下面是一个Asset的查询，用标签is_local=yes来过滤，查找本地local Core创建的所有所有符合条件的assets
    // 查询的结果放到localAssets
    Asset.Items localAssets = new Asset.QueryBuilder()
    // ???????????这里的=$是什么意思，是表示判断么
      .setFilter("is_local=$1")
      .addFilterParameter("yes")
      .execute(client);

    //下面这段代码的效果是把查询到的所有localAssets分别打印出来
    //hasNext()和Next()效果其实是一样的，系统都会等待输入下一个字符
    //只是返回值不同，hasNext()会返回true，next()返回输入的字符
    while (localAssets.hasNext()) {
      Asset asset = localAssets.next();
      System.out.println("Local asset: " + asset.alias);
    }
    // endsnippet

    // snippet list-private-preferred-securities
    // 创建一个新的查询叫preferred
    // 下面是一个Asset的查询，用Definition来过滤 type=security
    // subtype=private, class=preferred
    // 来过滤，查找本地local Core创建的所有符合条件的assets
    // 查询的结果放到preferred
    Asset.Items preferred = new Asset.QueryBuilder()
      .setFilter("definition.type=$1 AND definition.subtype=$2 AND definition.class=$3")
      .addFilterParameter("security")
      .addFilterParameter("private")
      .addFilterParameter("preferred")
      .execute(client);
    
    //下面这段代码的效果是把查询到的所有preferred分别打印出来
    //hasNext()和Next()效果其实是一样的，系统都会等待输入下一个字符
    //只是返回值不同，hasNext()会返回true，next()返回输入的字符
    while (preferred.hasNext()) {
      Asset asset = preferred.next();
      System.out.println("Private preferred security: " + asset.alias);
    }
    // endsnippet


    
    // snippet build-issue
    // 创建一个新的transaction叫做issuanceTransaction
    // 发行1000单元的acme_common到acme_treasury账户
    // Action名称为Issue
    Transaction.Template issuanceTransaction = new Transaction.Builder()
      .addAction(new Transaction.Action.Issue()
        .setAssetAlias("acme_common")
        .setAmount(1000)
      ).addAction(new Transaction.Action.ControlWithAccount()
        .setAccountAlias("acme_treasury")
        .setAssetAlias("acme_common")
        .setAmount(1000)
      ).build(client);
    // endsnippet

    // snippet sign-issue
    // 创建完transaction之后，签名，用Asset的Key通过HsmSigner签名
    Transaction.Template signedIssuanceTransaction = HsmSigner.sign(issuanceTransaction);
    // endsnippet

    // snippet submit-issue
    // 签名完了，提交此transaction，从此在blockchain中生效
    Transaction.submit(client, signedIssuanceTransaction);
    // endsnippet
    
    
    // 发行2000个单位的acme_preferred到外部单位
    // 发行之前，必须从外部单位(此处名为acme_treasury)请求一个新的
    // control program, 此处control program名为
    // externalProgram
    ControlProgram externalProgram = new ControlProgram.Builder()
      .controlWithAccountByAlias("acme_treasury")
      .create(client);

    // snippet external-issue
    // 创建一个新的transaction叫做externalIssuance
    // 发行2000单元的acme_preferred到名为externalProgram的
    // control program
    // Action名称为Issue
    Transaction.Template externalIssuance = new Transaction.Builder()
      .addAction(new Transaction.Action.Issue()
        .setAssetAlias("acme_preferred")
        .setAmount(2000)
      ).addAction(new Transaction.Action.ControlWithProgram()
        .setControlProgram(externalProgram)
        .setAssetAlias("acme_preferred")
        .setAmount(2000)
      ).build(client);
      
    // 创建完transaction之后，签名，用Asset的Key通过HsmSigner签名  
    // 签名完了，提交此transaction，从此在blockchain中生效
    // 以上两步可以合并在一起运行（签名+提交）
    Transaction.submit(client, HsmSigner.sign(externalIssuance));
    // endsnippet

    // snippet build-retire
    // 创建一个从acme_treasury账户退还50 acme_common单元的交易
    // 交易名称叫做retirementTransaction
    // Action名称为SpendFromAccount
    Transaction.Template retirementTransaction = new Transaction.Builder()
      .addAction(new Transaction.Action.SpendFromAccount()
        .setAccountAlias("acme_treasury")
        .setAssetAlias("acme_common")
        .setAmount(50)
      ).addAction(new Transaction.Action.Retire()
        .setAssetAlias("acme_common")
        .setAmount(50)
      ).build(client);
    // endsnippet

    // snippet sign-retire
    // 创建完transaction之后，签名，用创建acme_treasury账号的Key通过HsmSigner签名 
    Transaction.Template signedRetirementTransaction = HsmSigner.sign(retirementTransaction);
    // endsnippet

    // snippet submit-retire
    // 签名完了，提交此transaction，从此在blockchain中生效
    Transaction.submit(client, signedRetirementTransaction);
    // endsnippet

    // snippet list-issuances
    // 显示出所有发行的transaction的记录，asset=acme_common
    Transaction.Items acmeCommonIssuances = new Transaction.QueryBuilder()
      .setFilter("inputs(type=$1 AND asset_alias=$2)")
      .addFilterParameter("issue")
      .addFilterParameter("acme_common")
      .execute(client);

    while (acmeCommonIssuances.hasNext()) {
      Transaction tx = acmeCommonIssuances.next();
      System.out.println("Acme Common issued in tx " + tx.id);
    }
    // endsnippet

    // snippet list-transfers
    // 显示出所有转账的transaction的记录，asset=acme_common
    Transaction.Items acmeCommonTransfers = new Transaction.QueryBuilder()
      .setFilter("inputs(type=$1 AND asset_alias=$2)")
      .addFilterParameter("spend")
      .addFilterParameter("acme_common")
      .execute(client);

    while (acmeCommonTransfers.hasNext()) {
      Transaction tx = acmeCommonTransfers.next();
      System.out.println("Acme Common transferred in tx " + tx.id);
    }
    // endsnippet

    // snippet list-retirements
    // 显示出所有退款的transaction的记录，asset=acme_common
    Transaction.Items acmeCommonRetirements = new Transaction.QueryBuilder()
      .setFilter("outputs(type=$1 AND asset_alias=$2)")
      .addFilterParameter("retire")
      .addFilterParameter("acme_common")
      .execute(client);

    while (acmeCommonRetirements.hasNext()) {
      Transaction tx = acmeCommonRetirements.next();
      System.out.println("Acme Common retired in tx " + tx.id);
    }
    // endsnippet

    // snippet list-acme-common-balance
    Balance.Items acmeCommonBalances = new Balance.QueryBuilder()
      .setFilter("asset_alias=$1")
      .addFilterParameter("acme_common")
      .execute(client);

    Balance acmeCommonBalance = acmeCommonBalances.next();
    System.out.println("Total circulation of Acme Common: " + acmeCommonBalance.amount);
    // endsnippet

    // snippet list-acme-balance
    Balance.Items acmeAnyBalances = new Balance.QueryBuilder()
      .setFilter("asset_definition.issuer=$1")
      .addFilterParameter("Acme Inc.")
      .execute(client);

    while (acmeAnyBalances.hasNext()) {
      Balance stockBalance = acmeAnyBalances.next();
      System.out.println(
        "Total circulation of Acme stock " + stockBalance.sumBy.get("asset_alias") +
        ": " + stockBalance.amount
      );
    }
    // endsnippet

    // snippet list-acme-common-unspents
    UnspentOutput.Items acmeCommonUnspentOutputs = new UnspentOutput.QueryBuilder()
      .setFilter("asset_alias=$1")
      .addFilterParameter("acme_common")
      .execute(client);

    while (acmeCommonUnspentOutputs.hasNext()) {
      UnspentOutput utxo = acmeCommonUnspentOutputs.next();
      System.out.println("Acme Common held in output " + utxo.transactionId + ":" + utxo.position);
    }
    // endsnippet
  }
}