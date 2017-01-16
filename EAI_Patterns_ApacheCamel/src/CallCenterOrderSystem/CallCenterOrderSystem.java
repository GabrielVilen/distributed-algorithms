package CallCenterOrderSystem;

/**
 * the Call center order system generates a text
 * file containing new orders every 2 minutes and stores it at a pre­defined
 * destination in the local file system. Each line represents a single order.
 * An order consists of comma­separated entries formatted as <CustomerID,
 *   Full   Name,   Number   of   ordered   surfboards,   Number   of   ordered
 * diving suits> ­ e.g.: 1, Alice Test, 0, 1. The full name always consist of
 * the first and the last name separated by a space. It is not defined how
 * many orders are contained in the file. However, it is required that at
 * least   one   call   center   order   is   processed   during   presentation   of   the
 * exercise
 * <p>
 * Created by gabri on 2017-01-12.
 */
public class CallCenterOrderSystem {

    public CallCenterOrderSystem() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // generate a new text file

                    Thread.sleep(120000); // sleep 2 minutes
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

/**
 * To get started with Camel:
 * <p>
 * Create a CamelContext.
 * <p>
 * Optionally, configure components or endpoints.
 * <p>
 * Add whatever routing rules you wish using the DSL and RouteBuilder or using Xml Configuration.
 * <p>
 * Start the context.
 */
class Starter {
    public static void main(String[] args) {
    }
}