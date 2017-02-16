import java.util.*;

import com.chain.api.*;
import com.chain.http.*;
import com.chain.signing.*;


public class test {
    public static void main(String[] args) throws Exception {
        String Surfboard_company = "Surfboard_company";
        String SurfBoard = "SurfBoard";
        String NewAccount_Name;
        String Number_of_New_Order;
        String duplicateaccountname=null;
        String duplicateaccountid=null;
        int quantity=0;
        boolean hasAccount = false;
        boolean duplicatecustomeraccount = false;

        MockHsm.Key NewAccount_key = null;
        MockHsm.Key NewAccount_Dollar_key = null;
        MockHsm.Key NewAccount_Pledge_key = null;

        //创建一个叫Client的客户端
        Client client = new Client();

        //先检查是否已经有了Surfboard_company这个账号
        Account.Items accounts = new Account.QueryBuilder()
                .execute(client);

        while (accounts.hasNext()) {
            Account duplicate_account = accounts.next();


            if (duplicate_account.alias.equals(Surfboard_company)) {
                hasAccount = true;
            }

        }


        //如果还没有Surfboard_company这个账号，创建这个账号相应的Key, Asset, Account,并初始化库存
        if (!hasAccount) {

            //创建一个叫做Surfboard_company_key的key,并装载到HsmSigner
            MockHsm.Key Surfboard_company_key = MockHsm.Key.create(client);
            HsmSigner.addKey(Surfboard_company_key, MockHsm.getSignerClient(client));

            //创建一个叫做Surfboard_key的key,并装载到HsmSigner
            MockHsm.Key Surfboard__key = MockHsm.Key.create(client);
            HsmSigner.addKey(Surfboard__key, MockHsm.getSignerClient(client));

            new Asset.Builder()
                    .setAlias(SurfBoard)
                    .addRootXpub(Surfboard__key.xpub)
                    .setQuorum(1)
                    .addDefinitionField("issuer", Surfboard_company)
                    .create(client);


            new Account.Builder()
                    .setAlias(Surfboard_company)
                    .addRootXpub(Surfboard_company_key.xpub)
                    .setQuorum(1)
                    .create(client);

            Transaction.Template initial_Surfboard_Inventory = new Transaction.Builder()
                    .addAction(new Transaction.Action.Issue()
                            .setAssetAlias(SurfBoard)
                            .setAmount(100)
                    ).addAction(new Transaction.Action.ControlWithAccount()
                            .setAccountAlias(Surfboard_company)
                            .setAssetAlias(SurfBoard)
                            .setAmount(100)
                    ).build(client);
            //System.out.println("签名之前");
            Transaction.Template signedRetirementTransaction = HsmSigner.sign(initial_Surfboard_Inventory);
            //System.out.println("提交之后");
            Transaction.submit(client, signedRetirementTransaction);

            //System.out.println("充值完了冲浪板");
        }


        //用户输入姓名
        System.out.println("Welcome to the online surfboard rental portal" + "\n" + "Please type in your name:");
        Scanner NewAccount = new Scanner(System.in);
        NewAccount_Name = NewAccount.nextLine();


        String NewAccount_Pledge = NewAccount_Name + "_Pledge";
        String NewAccount_Dollar = NewAccount_Name + "_Dollar";

        //查询数据库中是否已经有了此姓名
        Account.Items oldaccountcheck = new Account.QueryBuilder()
                .execute(client);

        while (oldaccountcheck.hasNext()) {
            Account a = oldaccountcheck.next();
            if (a.alias.equals(NewAccount_Name)) {
                duplicateaccountid = a.id;
                duplicateaccountname = a.alias;
                duplicatecustomeraccount=true;
            }
        }

        //如果数据库中已经有了此姓名的用户，直接让用户输入需要租用的冲浪板数目
        if (duplicatecustomeraccount) {


            System.out.println("You already have an account!" + "\n" + "Account ID:" + duplicateaccountid + ", Account Name:" + duplicateaccountname);

            Balance.Items balances = new Balance.QueryBuilder()
                    .setFilter("account_alias=$1")
                    .addFilterParameter(NewAccount_Name)
                    .execute(client);

            while (balances.hasNext()) {
                Balance b = balances.next();
                System.out.println(
                        NewAccount_Name+"'s balance of " + b.sumBy.get("asset_alias") +
                                ": " + b.amount
                );
            }


            // 用户输入需要租用的冲浪板数目
            System.out.println("Welcome " + NewAccount_Name + "\n" + "Please type in your number of ordering:");
            Scanner New_Order = new Scanner(System.in);
            Number_of_New_Order = New_Order.nextLine();
            quantity = Integer.parseInt(Number_of_New_Order);

            System.out.println("测试老账号已经存在成功");


        //如果数据库中还没有此用户，则为用户创建相应的Key, Asset, Account,并初始化用户的Pledge和钱数目
        } else {
            // 用户输入需要租用的冲浪板数目
            System.out.println("Welcome " + NewAccount_Name + "\n" + "Your account name is: " + NewAccount_Name + "\n" + "Please type in your number of ordering:");
            Scanner New_Order = new Scanner(System.in);
            Number_of_New_Order = New_Order.nextLine();
            quantity = Integer.parseInt(Number_of_New_Order);


            // 提示用户当前的订单
            System.out.println("Thanks " + NewAccount_Name + "!!!" + "\n" + "You will rent : " + Number_of_New_Order + " Surfboard" + ", have a good day!!! ");


            //为该用户创建相应的Key
            NewAccount_key = MockHsm.Key.create(client);
            HsmSigner.addKey(NewAccount_key, MockHsm.getSignerClient(client));

            NewAccount_Dollar_key = MockHsm.Key.create(client);
            HsmSigner.addKey(NewAccount_Dollar_key, MockHsm.getSignerClient(client));

            NewAccount_Pledge_key = MockHsm.Key.create(client);
            HsmSigner.addKey(NewAccount_Pledge_key, MockHsm.getSignerClient(client));

            //为该用户创建相应的账号
                new Account.Builder()
                        .setAlias(NewAccount_Name)
                        .addRootXpub(NewAccount_key.xpub)
                        .setQuorum(1)
                        .create(client);

                System.out.println("新账号创建成功");

            //为该用户创建相应的Asset
                new Asset.Builder()
                        .setAlias(NewAccount_Dollar)
                        .addRootXpub(NewAccount_Dollar_key.xpub)
                        .setQuorum(1)
                        .create(client);


                new Asset.Builder()
                        .setAlias(NewAccount_Pledge)
                        .addRootXpub(NewAccount_Pledge_key.xpub)
                        .setQuorum(1)
                        .create(client);


            //为该用户初始化账户余额
                int rn = 0;
                Random random = new Random();
                rn = random.nextInt(100) + 100;

                Transaction.Template Money_in_Customer_Wallet = new Transaction.Builder()
                        .addAction(new Transaction.Action.Issue()
                                .setAssetAlias(NewAccount_Dollar)
                                .setAmount(rn)
                        ).addAction(new Transaction.Action.ControlWithAccount()
                                .setAccountAlias(NewAccount_Name)
                                .setAssetAlias(NewAccount_Dollar)
                                .setAmount(rn)
                        ).build(client);


                Transaction.submit(client, HsmSigner.sign(Money_in_Customer_Wallet));

                System.out.println("新账号的钱添加成功");

                Transaction.Template Customer_sign_Pledge = new Transaction.Builder()
                        .addAction(new Transaction.Action.Issue()
                                .setAssetAlias(NewAccount_Pledge)
                                .setAmount(1)
                        ).addAction(new Transaction.Action.ControlWithAccount()
                                .setAccountAlias(NewAccount_Name)
                                .setAssetAlias(NewAccount_Pledge)
                                .setAmount(1)
                        ).build(client);
                Transaction.submit(client, HsmSigner.sign(Customer_sign_Pledge));

                System.out.println("新账号的账本添加成功");
        }

        System.out.println("开始租赁交易之前");


        MockHsm.Key.Items Previouskey= new MockHsm.Key.QueryBuilder().execute(client);

        while(Previouskey.hasNext()){
            MockHsm.Key x=Previouskey.next();
            HsmSigner.addKey(x, MockHsm.getSignerClient(client));
        }

        //开始租赁交易
        Transaction.Template rent_trade = new Transaction.Builder()
                .addAction(new Transaction.Action.SpendFromAccount()
                        .setAccountAlias(NewAccount_Name)
                        .setAssetAlias(NewAccount_Dollar)
                        .setAmount(15*quantity)
                ).addAction(new Transaction.Action.SpendFromAccount()
                        .setAccountAlias(NewAccount_Name)
                        .setAssetAlias(NewAccount_Pledge)
                        .setAmount(1)
                ).addAction(new Transaction.Action.SpendFromAccount()
                        .setAccountAlias(Surfboard_company)
                        .setAssetAlias(SurfBoard)
                        .setAmount(quantity)
                ).addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountAlias(Surfboard_company)
                        .setAssetAlias(NewAccount_Dollar)
                        .setAmount(15*quantity)
                ).addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountAlias(Surfboard_company)
                        .setAssetAlias(NewAccount_Pledge)
                        .setAmount(1)
                ).addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountAlias(NewAccount_Name)
                        .setAssetAlias(SurfBoard)
                        .setAmount(quantity)
                ).build(client);


        System.out.println("准备签字");

        Transaction.Template signedMultiAssetPayment = HsmSigner.sign(rent_trade);


        System.out.println("已签字，准备提交");

        Transaction.submit(client, signedMultiAssetPayment);

        System.out.println("交易提交完成");


        Boolean In_Rent = true;

        while (In_Rent) {
            System.out.println("Do you want to return the SurfBoard you rented now?");
            System.out.println("Press 1 for return, press 2 for return later");
            System.out.println("Please enter:");
            Scanner Customer_Type = new Scanner(System.in);
            String customer_input = Customer_Type.nextLine();
            if (customer_input.equals("1")) {
                In_Rent = false;

                Transaction.Template return_surfboard = new Transaction.Builder()
                        .addAction(new Transaction.Action.SpendFromAccount()
                                .setAccountAlias(NewAccount_Name)
                                .setAssetAlias(SurfBoard)
                                .setAmount(quantity)
                        ).addAction(new Transaction.Action.SpendFromAccount()
                                .setAccountAlias(Surfboard_company)
                                .setAssetAlias(NewAccount_Pledge)
                                .setAmount(1)
                        ).addAction(new Transaction.Action.ControlWithAccount()
                                .setAccountAlias(Surfboard_company)
                                .setAssetAlias(SurfBoard)
                                .setAmount(quantity)
                        ).addAction(new Transaction.Action.ControlWithAccount()
                                .setAccountAlias(NewAccount_Name)
                                .setAssetAlias(NewAccount_Pledge)
                                .setAmount(1)
                        ).build(client);

                Transaction.submit(client, HsmSigner.sign(return_surfboard));

            }
        }


        System.out.println("Thank you very much wish you a good day!!!");

    }
}
