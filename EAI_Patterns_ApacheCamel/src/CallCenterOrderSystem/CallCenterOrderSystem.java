package CallCenterOrderSystem;

import Order.Order;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TypeConversionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
 */
public class CallCenterOrderSystem implements Processor {

    private List<String> toWrite = new ArrayList<String>();
    private static final String FILE_PATH = "orders/order_";
    private static final String TCP_LOCALHOST_61616 = "tcp://localhost:61616";
    private static final String CC_NEW_ORDER = "activemq:queue:CC_NEW_ORDER";
    private static final String NEW_ORDER = "activemq:queue:NEW_ORDER";
    private int ordNumber;

    /**
     * Constructor that creates new thread that writes to file
     */
    public CallCenterOrderSystem() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000); // sleep 2 minutes = 120000ms before write to file
                        writeFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    /**
     * The process method acts as the Message Translator from type String to Order
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            String[] parts = exchange.getIn().getBody(String.class).split(", ");

            Order order = new Order();
            order.setCustomerID(parts[0]);
            String[] name = parts[1].split(" ");

            order.setFirstName(name[0]);
            order.setLastName(name[1]);
            int divingSuits = Integer.parseInt(parts[2]);
            int surfboards = Integer.parseInt(parts[3].substring(0, 1));
            order.setNumberOfDivingSuits(divingSuits);
            order.setNumberOfSurfboards(surfboards);
            order.setOverallItems(divingSuits + surfboards);

            System.out.println("Set message body to: " + order.toString());
            exchange.getIn().setBody(order);
        } catch (TypeConversionException ex) {
            System.err.println("Message type not Order " + ex.getMessage());
        }

    }

    /**
     * An order string consists of comma­separated entries formatted as "CustomerID,
     *   Full   Name,   Number   of   ordered   surfboards,   Number   of   ordered
     * diving suits" 
     */
    public void addOrder(int id, String fullName, int surfboards, int divingsuits) {
        toWrite.add(id + ", " + fullName + ", " + surfboards + ", " + divingsuits);
    }

    @SuppressWarnings("Since15")
    private void writeFile() throws IOException {
        Path file = Paths.get(FILE_PATH + ordNumber);
        Files.write(file.toAbsolutePath(), toWrite, Charset.forName("UTF-8"));

        System.out.println("Wrote " + toWrite + " to file " + file);

        ordNumber++;
        toWrite.clear();

    }


    /**
     * Start Call center order system that reads from file and delete it
     */
    public static void main(String[] args) {
        try {
            final CallCenterOrderSystem orderConsumer = new CallCenterOrderSystem();

            // Create Camel Context
            DefaultCamelContext camelContext = new DefaultCamelContext();

            // Connect localhost ActiveMQ which should be separate process apache-activemq-5.14.3/bin$ ./activemq console
            ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent(TCP_LOCALHOST_61616);
            camelContext.addComponent("activemq", activeMQComponent);
            camelContext.addRoutes(new RouteBuilder(camelContext) {
                @Override
                public void configure() throws Exception {
                    // send and delete files from directory
                    from("file:orders?delete=true")
                            .split(body().tokenize("\n"))
                            .process(orderConsumer)
                            .to(NEW_ORDER);
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

    /**
     * Tests the system by putting random orders on its queue
     */
    private static void testSystem(final CallCenterOrderSystem orderSystem) throws Exception {

        // generate new orders to the call center
        new Thread(new Runnable() {
            public void run() {
                int id = 0;
                Random random = new Random();
                while (true) {
                    String fullName = "Alice Bobbson";
                    int surfboards = random.nextInt(10);
                    int divingsuits = random.nextInt(10);
                    orderSystem.addOrder(id++, fullName, surfboards, divingsuits);
                    try {
                        Thread.sleep(1000); // 30000
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();
    }

}
