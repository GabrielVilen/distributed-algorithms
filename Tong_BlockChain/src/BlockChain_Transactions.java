import java.util.*;

import com.chain.api.*;
import com.chain.http.*;
import com.chain.signing.*;


public class BlockChain_Transactions {
    public static void main(String[] args) throws Exception {
        //    int intArray[] = new int[5];
        //    long total = 0;
        //    int len = intArray.length;
        String Surfboard_company = "Surfboard_company";
        String SurfBoard = "SurfBoard";
        String NewAccount_Name;
        String Number_of_New_Order;
        String duplicateaccountname=null;
        String duplicateaccountid=null;
        int quantity=0;

        boolean hasAccount = false;
        boolean duplicatecustomeraccount = false;


        HashMap<String, List<MockHsm.Key>> map = new HashMap<>();
        HashMap<String, List<String>> asset_map = new HashMap<>();

        MockHsm.Key NewAccount_key = null;
        MockHsm.Key NewAccount_Dollar_key = null;
        MockHsm.Key NewAccount_Pledge_key = null;

        //创建一个叫Client的客户端
        Client client = new Client();

        Account.Items accounts = new Account.QueryBuilder()
                .execute(client);

        while (accounts.hasNext()) {
            Account duplicate_account = accounts.next();


            if (duplicate_account.alias.equals(Surfboard_company)) {
                hasAccount = true;
            }

        }
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


        // 用户注册，用户输入姓名
        System.out.println("Welcome to the online surfboard rental portal" + "\n" + "Please type in your name:");
        Scanner NewAccount = new Scanner(System.in);
        NewAccount_Name = NewAccount.nextLine();


        Account.Items oldaccountcheck = new Account.QueryBuilder()
                .execute(client);

        String NewAccount_Pledge = NewAccount_Name + "_Pledge";
        String NewAccount_Dollar = NewAccount_Name + "_Dollar";


        while (oldaccountcheck.hasNext()) {
            Account a = oldaccountcheck.next();
            if (a.alias.equals(NewAccount_Name)) {
                duplicateaccountid = a.id;
                duplicateaccountname = a.alias;
                duplicatecustomeraccount=true;
                System.out.println(duplicatecustomeraccount);

            }
        }


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
                System.out.println("Welcome " + NewAccount_Name + "\n" + "How much surfboards do you want to rent?");
                Scanner New_Order = new Scanner(System.in);
                Number_of_New_Order = New_Order.nextLine();
                quantity = Integer.parseInt(Number_of_New_Order);

                System.out.println("测试老账号已经存在成功");

            } else {

                // 用户输入需要租用的冲浪板数目
                System.out.println("Welcome " + NewAccount_Name + "\n" + "Your account name is: " + NewAccount_Name + "\n" + "Please type in your number of ordering:");
                Scanner New_Order = new Scanner(System.in);
                Number_of_New_Order = New_Order.nextLine();
                quantity = Integer.parseInt(Number_of_New_Order);


                // 提示用户当前的订单
                System.out.println("Thanks " + NewAccount_Name + "!!!" + "\n" + "You will rent : " + Number_of_New_Order + " Surfboard" + ", have a good day!!! ");


                if (map.containsKey(NewAccount_Name)) {
                    //NewAccount_key = map.get(NewAccount_Name);
                } else {
                    NewAccount_key = MockHsm.Key.create(client);
                    HsmSigner.addKey(NewAccount_key, MockHsm.getSignerClient(client));


                    NewAccount_Dollar_key = MockHsm.Key.create(client);
                    HsmSigner.addKey(NewAccount_Dollar_key, MockHsm.getSignerClient(client));

                    NewAccount_Pledge_key = MockHsm.Key.create(client);
                    HsmSigner.addKey(NewAccount_Pledge_key, MockHsm.getSignerClient(client));

                    ArrayList<MockHsm.Key> keys = new ArrayList<>();
                    keys.add(NewAccount_key);
                    keys.add(NewAccount_Dollar_key);
                    keys.add(NewAccount_Pledge_key);

                    // map.put(NewAccount_Name, keys);


                    //创建一个新的账号，叫Surfboard_company，添加accountKey(key)
                    //设置Quorum为1


                    new Account.Builder()
                            .setAlias(NewAccount_Name)
                            .addRootXpub(NewAccount_key.xpub)
                            .setQuorum(1)
                            .create(client);

                    System.out.println("新账号创建成功");


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


            }


        MockHsm.Key.Items PreviousKey= new MockHsm.Key.QueryBuilder().execute(client);

        while(PreviousKey.hasNext()){
            MockHsm.Key x=PreviousKey.next();
            HsmSigner.addKey(x, MockHsm.getSignerClient(client));
        }


                System.out.println("开始交易之前");

                Transaction.Template rent_trade = new Transaction.Builder()
                        .addAction(new Transaction.Action.SpendFromAccount()
                                .setAccountAlias(NewAccount_Name)
                                .setAssetAlias(NewAccount_Dollar)
                                .setAmount(15)
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
                                .setAmount(15)
                        ).addAction(new Transaction.Action.ControlWithAccount()
                                .setAccountAlias(Surfboard_company)
                                .setAssetAlias(NewAccount_Pledge)
                                .setAmount(1)
                        ).addAction(new Transaction.Action.ControlWithAccount()
                                .setAccountAlias(NewAccount_Name)
                                .setAssetAlias(SurfBoard)
                                .setAmount(quantity)
                        ).build(client);


                System.out.println("不能签字提交");

                Transaction.submit(client, HsmSigner.sign(rent_trade));

                System.out.println("签字提交完成");


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
