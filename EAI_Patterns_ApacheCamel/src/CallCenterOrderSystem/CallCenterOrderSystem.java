package CallCenterOrderSystem;

import Order.Order;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * uses Channel Adapter 
 * <p>
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

    private final Path file;
    private List<String> lines = new ArrayList<>();
    private static final String FILE_PATH = "order-file.txt";
    private static final String TCP_LOCALHOST_61616 = "tcp://localhost:61616";
    private static final String CC_NEW_ORDER = "activemq:queue:CC_NEW_ORDER";
    private static final String NEW_ORDER = "activemq:queue:NEW_ORDER";

    public static void main(String[] args) {
        try {
            final Path file = Paths.get(FILE_PATH);
            final CallCenterOrderSystem orderConsumer = new CallCenterOrderSystem(file);

            // Create Camel Context
            DefaultCamelContext camelContext = new DefaultCamelContext();

            // Connect localhost ActiveMQ which should be separate process apache-activemq-5.14.3/bin$ ./activemq console
            ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent(TCP_LOCALHOST_61616);
            camelContext.addComponent("activemq", activeMQComponent);
            camelContext.addRoutes(new RouteBuilder(camelContext) {
                @Override
                public void configure() throws Exception {
                    fromF("file://" + FILE_PATH).to(NEW_ORDER); // create endpoint from file
                }
            });
            camelContext.start();

            testSystem(orderConsumer);

            System.in.read(); // wait till ENTER pressed

            camelContext.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testSystem(CallCenterOrderSystem orderSystem) throws Exception {

        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.setFirstName("Alice_" + i);
            order.setLastName("test_" + i);
            orderSystem.addOrder(order);
        }
    }


    public CallCenterOrderSystem(Path file) {
        this.file = file;

        // creates new thread that writes to file
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000); // sleep 2 minutes = 120000ms before write to file
                    writeFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void addOrder(Order order) {
        String ordStr = order.getFirstName() + ", " + order.getLastName() + ", " + order.getNumberOfSurfboards()
                + ", " + order.getNumberOfSurfboards() + ", " + order.getCustomerID();
        lines.add(ordStr);
    }

    @SuppressWarnings("Since15")
    private void writeFile() throws IOException {
        System.out.println("Trying to write to file = " + file.toAbsolutePath());

        Charset charset = Charset.forName("US-ASCII");
        String s = "blablabla";
        try (BufferedWriter writer = Files.newBufferedWriter(file.toAbsolutePath(), charset)) {
            writer.write(s, 0, s.length());
            System.out.println("Wrote line to file:  " + s);
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        //   Files.write(file.toAbsolutePath(), lines, Charset.forName("UTF-8")); // TODO: causes java.nio.file.AccessDeniedException:
    }
}
