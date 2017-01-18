package WebOrderSystem;

import Order.Order;

import java.util.Random;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * Uses Message Endpoint
 * <p>
 * The web order system generates a string for each
 * new incoming order. The string needs to be processed further by the
 * integration solution. The string consists of comma separated entries and
 * is formatted as follows: <First Name, Last Name, Number of ordered
 * surfboards, Number of ordered diving suits, Customer­ID> ­ e.g.: Alice,
 * Test, 2, 0, 1
 * <p>
 * Created by gabri on 2017-01-12.
 */
class WebOrderSystem implements Processor {

    private static final String TCP_LOCALHOST_61616 = "tcp://localhost:61616";
    private static final String WEB_NEW_ORDER = "activemq:queue:WEB_NEW_ORDER";
    private static final String NEW_ORDER = "activemq:queue:NEW_ORDER";

    /**
     * The process method acts as the Message Translator from type Order to String
     */
    public void process(Exchange exchange) throws Exception {
        try {
            String[] parts = exchange.getIn().getBody(String.class).split(",");

            Order order = new Order();
            order.setFirstName(parts[0]);
            order.setLastName(parts[1]);
            
            int divingSuits = Integer.parseInt(parts[2]);
            int surfboards = Integer.parseInt(parts[3]);
            order.setOverallItems(divingSuits+surfboards);
            order.setNumberOfDivingSuits(divingSuits);
            order.setNumberOfSurfboards(surfboards);
            order.setCustomerID(parts[4]);

            System.out.println("Set message body to: " + order.toString());
            exchange.getIn().setBody(order);
        } catch (TypeConversionException ex) {
            System.err.println("Message type not Order " + ex.getMessage());
        }

    }

    public static void main(String[] args) {
        try {
            final WebOrderSystem orderConsumer = new WebOrderSystem();

            // Create Camel Context
            DefaultCamelContext camelContext = new DefaultCamelContext();

            // Connect localhost ActiveMQ which should be separate process apache-activemq-5.14.3/bin$ ./activemq console
            ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent(TCP_LOCALHOST_61616);
            camelContext.addComponent("activemq", activeMQComponent);
            camelContext.addRoutes(new RouteBuilder(camelContext) {
                @Override
                public void configure() throws Exception {
                    from(WEB_NEW_ORDER).process(orderConsumer).to(NEW_ORDER); // creates point-to-point channel
                }
            });
            camelContext.getEndpoint(WEB_NEW_ORDER).createConsumer(orderConsumer);
            camelContext.start();

            testQueue(camelContext);

            System.in.read(); // wait till ENTER pressed

            camelContext.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testQueue(CamelContext camelContext) throws Exception {

        ProducerTemplate template = camelContext.createProducerTemplate();
        Random rn = new Random();

        for (int i = 0; i < 10; i++) {
            int numberOfDivingSuits = rn.nextInt(10);
            int numberOfSurfboards = rn.nextInt(10);
            String customerID = Integer.toString(rn.nextInt(100));
            
            String ordStr = ("Alice_" + i) + "," + ("Surname_" + i) + "," + numberOfSurfboards
            + "," + numberOfDivingSuits + "," + customerID;
            
            template.sendBody(WEB_NEW_ORDER, ordStr);
            System.out.println("Sent order: " + ordStr);
        }
        template.stop();
    }
}