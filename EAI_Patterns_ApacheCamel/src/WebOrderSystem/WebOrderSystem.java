package WebOrderSystem;

import Order.Order;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.TypeConversionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.*;

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
public class WebOrderSystem implements WebOrderInterface, Processor {

    private Connection connection;
    private MessageProducer producer;
    private Session session;

    public static final String TCP_LOCALHOST_61616 = "tcp://localhost:61616";
    public static final String WEB_NEW_ORDER = "activemq:queue:WEB_NEW_ORDER";
    public static final String NEW_ORDER = "activemq:queue:NEW_ORDER";


    public WebOrderSystem() {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(TCP_LOCALHOST_61616);

            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("WEB_NEW_ORDER");

            // Create a MessageProducer from the Session to the Topic or Queue
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Clean up
    public void cleanUp() throws JMSException {
        session.close();
        connection.close();
    }

    /**
     * The process method acts as the Message Translator from type Order to String
     */
    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getIn();
        try {
            Order order = message.getBody(Order.class); // type conversion to Order.class

            String ordStr = order.getFirstName() + ", " + order.getLastName() + ", " + order.getNumberOfSurfboards()
                    + ", " + order.getNumberOfDivingSuits() + ", " + order.getCustomerID();

            message.setBody(ordStr);
        } catch (TypeConversionException ex) {
            System.err.println("Message type not Order: " + ex.getMessage());
        }

        System.out.println("Set message body to: " + message.getBody());
    }


    @Override
    public void addOrder(Order order) {
        try {
            ObjectMessage message = session.createObjectMessage(order);

            // Tell the producer to send the message
            System.out.println("Sent message to: " + producer.getDestination());
            producer.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            // Create Camel Context
            DefaultCamelContext camelContext = new DefaultCamelContext();
            final WebOrderSystem webOrderSystem = new WebOrderSystem();


            // Connect localhost ActiveMQ which should be separate process apache-activemq-5.14.3/bin$ ./activemq console
            ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent(TCP_LOCALHOST_61616);
            camelContext.addComponent("activemq", activeMQComponent);
            camelContext.addRoutes(new RouteBuilder(camelContext) {
                @Override
                public void configure() throws Exception {
                    from(WEB_NEW_ORDER).process(webOrderSystem).to(NEW_ORDER); // creates point-to-point channel
                }
            });
            camelContext.start();

            testQueue(webOrderSystem);

            System.in.read(); // wait till ENTER pressed

            camelContext.stop();
            webOrderSystem.cleanUp();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method acts as a test producer to the WEB_ORDER queue by putting new orders on the queue
     */
    private static void testQueue(WebOrderInterface web) throws Exception {
        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.setFirstName("Alice_" + i);
            order.setLastName("test_" + i);
            web.addOrder(order);
        }
    }

}

