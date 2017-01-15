package WebOrderSystem;

import Order.Order;
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

    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getIn();
        Order order = message.getBody(Order.class); // type conversion to Order.class

        String ordStr = order.getFirstName() + ", " + order.getLastName() + ", " + order.getNumberOfSurfboards()
                + ", " + order.getNumberOfDivingSuits() + ", " + order.getCustomerID();

        message.setBody(ordStr);

        System.out.println("Set message body to: " + message.getBody());
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

    private static final String IN_ENDPOINT_URL = "tcp://Gabriel:61616";
    private static final String QUEUE_1_URI = "activemq:queue:myqueue1";
    private static final String QUEUE_2_URI = "activemq:queue:myqueue2";

    public static void main(String[] args) {
        try {
            final WebOrderSystem orderConsumer = new WebOrderSystem();

            DefaultCamelContext camelContext = new DefaultCamelContext();
            ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent(IN_ENDPOINT_URL);

            camelContext.addComponent("activemq", activeMQComponent);
            camelContext.addRoutes(new RouteBuilder(camelContext) {
                @Override
                public void configure() throws Exception {
                    from(QUEUE_1_URI).process(orderConsumer).to(QUEUE_2_URI); // creates point-to-point channel
                }
            });
            camelContext.getEndpoint(QUEUE_1_URI).createConsumer(orderConsumer);
            camelContext.start();

            testQueue(camelContext);

            Thread.sleep(60000);

            camelContext.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testQueue(CamelContext camelContext) throws Exception {

        ProducerTemplate template = camelContext.createProducerTemplate();

        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.setFirstName("Alice_" + i);
            template.sendBody(QUEUE_1_URI, order);
            System.out.println("Sent msg " + i);
        }
        template.stop();
    }
}