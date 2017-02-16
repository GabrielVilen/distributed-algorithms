import java.util.*;
import java.net.*;

import com.chain.api.*;
import com.chain.http.*;
import com.chain.signing.*;

class new5mg {
    public static void main(String[] args) throws Exception {
        // This demo is written to run on either one or two cores. Simply provide
        // different URLs to the following clients for the two-core version.
        Client client = new Client();


        String aliceDollarAssetId=null;
        String bobBuckAssetId=null;
        String aliceAccountId=null;
        String bobAccountId=null;

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

        System.out.println("1");

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


        System.out.println("2");

        // snippet sign-trade-alice
        Transaction.Template aliceTradeSigned = HsmSigner.sign(aliceTrade.allowAdditionalActions());
        // endsnippet


        System.out.println("3");

        // snippet base-transaction-alice
        String baseTransactionFromAlice = aliceTradeSigned.rawTransaction;
        // endsnippet

        System.out.println("4");


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

        System.out.println("5");

        // snippet sign-trade-bob
        Transaction.Template bobTradeSigned = HsmSigner.sign(bobTrade);
        // endsnippet

        System.out.println("6");

        // snippet submit-trade-bob
        Transaction.submit(client, bobTradeSigned);



    }

}