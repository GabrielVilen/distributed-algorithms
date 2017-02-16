import java.util.*;
import java.net.*;

import com.chain.api.*;
import com.chain.http.*;
import com.chain.signing.*;

class query {
    public static void main(String[] args) throws Exception {
        // This demo is written to run on either one or two cores. Simply provide
        // different URLs to the following clients for the two-core version.
        Client client = new Client();

        String test;
        String Xpub;

        MockHsm.Key bobBuckKey = MockHsm.Key.create(client);
        HsmSigner.addKey(bobBuckKey, MockHsm.getSignerClient(client));

        MockHsm.Key.Items keyquery = new MockHsm.Key.QueryBuilder()
                .execute(client);


        while (keyquery.hasNext()) {
            MockHsm.Key a = keyquery.next();
            test= a.alias;
            Xpub=a.xpub;
            System.out.println(test+"\n"+Xpub);






        }



    }

}