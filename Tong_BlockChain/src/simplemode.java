import java.util.*;

import com.chain.api.*;
import com.chain.http.*;
import com.chain.signing.*;


public class simplemode {
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


        //创建一个叫Client的客户端
        Client client = new Client();



        //////////////////////////////测试. 大概是线上，线下不同步的问题？？？？？
        Transaction.Template test_dollar = new Transaction.Builder()
                .addAction(new Transaction.Action.SpendFromAccount()
                        .setAccountId("acc0RWD5QPBG090C")
                        .setAssetId("f6bc4e674f38f5e0a6c932e7f2c15394525a35f4645d6ac8657e5f4661a373ce")
                        .setAmount(15)

                ).addAction(new Transaction.Action.ControlWithAccount()
                        .setAccountId("acc0RWD5JGT00906")
                        .setAssetId("f6bc4e674f38f5e0a6c932e7f2c15394525a35f4645d6ac8657e5f4661a373ce")
                        .setAmount(15)
                ).build(client);
        Transaction.submit(client, HsmSigner.sign(test_dollar));



        System.out.println("Thank you very much wish you a good day!!!");

    }
}
