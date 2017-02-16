import java.util.*;
import java.net.*;

import com.chain.api.*;
import com.chain.http.*;
import com.chain.signing.*;

class MultipartyTrades {
    public static void main(String[] args) throws Exception {
        // This demo is written to run on either one or two cores. Simply provide
        // different URLs to the following clients for the two-core version.
        Client client = new Client();


        String aliceDollarAssetId=null;
        String bobBuckAssetId=null;
        String aliceAccountId=null;
        String bobAccountId=null;


        MockHsm.Key aliceDollarKey = MockHsm.Key.create(client);
        HsmSigner.addKey(aliceDollarKey, MockHsm.getSignerClient(client));
        aliceDollarKey.alias="aliceDollarKeynickname";


        MockHsm.Key bobBuckKey = MockHsm.Key.create(client);
        HsmSigner.addKey(bobBuckKey, MockHsm.getSignerClient(client));
        bobBuckKey.alias="bobBuckKeynickname";

        MockHsm.Key aliceKey = MockHsm.Key.create(client);
        HsmSigner.addKey(aliceKey, MockHsm.getSignerClient(client));
        aliceKey.alias="aliceaccountkeynickname";

        MockHsm.Key bobKey = MockHsm.Key.create(client);
        HsmSigner.addKey(bobKey, MockHsm.getSignerClient(client));
        bobKey.alias="bobaccountkeynickname";

        Asset aliceDollar = new Asset.Builder()
                .setAlias("aliceDollar")
                .addRootXpub(aliceDollarKey.xpub)
                .setQuorum(1)
                .create(client);

        Asset bobBuck = new Asset.Builder()
                .setAlias("bobBuck")
                .addRootXpub(bobBuckKey.xpub)
                .setQuorum(1)
                .create(client);

        Account alice = new Account.Builder()
                .setAlias("alice")
                .addRootXpub(aliceKey.xpub)
                .setQuorum(1)
                .create(client);

        Account bob = new Account.Builder()
                .setAlias("bob")
                .addRootXpub(bobKey.xpub)
                .setQuorum(1)
                .create(client);

        Transaction.submit(client, HsmSigner.sign(new Transaction.Builder()
                .addAction(new Transaction.Action.Issue()
                        .setAssetAlias("aliceDollar")
                        .setAmount(10000)
                ).addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountAlias("alice")
                        .setAssetAlias("aliceDollar")
                        .setAmount(10000)
                ).build(client)
        ));

        Transaction.submit(client, HsmSigner.sign(new Transaction.Builder()
                .addAction(new Transaction.Action.Issue()
                        .setAssetAlias("bobBuck")
                        .setAmount(10000)
                ).addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountAlias("bob")
                        .setAssetAlias("bobBuck")
                        .setAmount(10000)
                ).build(client)
        ));





        Asset.Items AliceAssets = new Asset.QueryBuilder()
                .setFilter("alias=$1")
                .addFilterParameter("aliceDollar")
                .execute(client);

        while (AliceAssets.hasNext()) {
            Asset asset = AliceAssets.next();

            aliceDollarAssetId=asset.id;
            System.out.println(aliceDollarAssetId);
        }


        Asset.Items BobAssets = new Asset.QueryBuilder()
                .setFilter("alias=$1")
                .addFilterParameter("bobBuck")
                .execute(client);

        while (BobAssets.hasNext()) {
            Asset asset = BobAssets.next();

            bobBuckAssetId=asset.id;
            System.out.println(bobBuckAssetId);
        }



        Account.Items AliceAccounts = new Account.QueryBuilder()
                .setFilter("alias=$1")
                .addFilterParameter("alice")
                .execute(client);

        while (AliceAccounts.hasNext()) {
            Account accountname = AliceAccounts.next();
            aliceAccountId=accountname.id;

            System.out.println(aliceAccountId);
        }



        Account.Items BobAccountshanshu = new Account.QueryBuilder()
                .setFilter("alias=$1")
                .addFilterParameter("bob")
                .execute(client);

        while (BobAccountshanshu.hasNext()) {
            Account accountname = BobAccountshanshu.next();
            bobAccountId=accountname.id;
            System.out.println(bobAccountId);

        }



        // snippet build-trade-alice
        Transaction.Template aliceTrade = new Transaction.Builder()
                .addAction(new Transaction.Action.SpendFromAccount()
                        .setAccountId(aliceAccountId)
                        .setAssetId(aliceDollarAssetId)
                        .setAmount(5)
                ).addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountId(aliceAccountId)
                        .setAssetId(bobBuckAssetId)
                        .setAmount(1)
                ).build(client);
        // endsnippet

        // snippet sign-trade-alice
        Transaction.Template aliceTradeSigned = HsmSigner.sign(aliceTrade.allowAdditionalActions());
        // endsnippet

        // snippet base-transaction-alice
        String baseTransactionFromAlice = aliceTradeSigned.rawTransaction;
        // endsnippet

        // snippet build-trade-bob
        Transaction.Template bobTrade = new Transaction.Builder()
                .setBaseTransaction(baseTransactionFromAlice)
                .addAction(new Transaction.Action.SpendFromAccount()
                        .setAccountId(bobAccountId)
                        .setAssetId(bobBuckAssetId)
                        .setAmount(1)
                ).addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountId(bobAccountId)
                        .setAssetId(aliceDollarAssetId)
                        .setAmount(5)
                ).build(client);
        // endsnippet

        // snippet sign-trade-bob
        Transaction.Template bobTradeSigned = HsmSigner.sign(bobTrade);
        // endsnippet

        // snippet submit-trade-bob
        Transaction.submit(client, bobTradeSigned);





    }

}