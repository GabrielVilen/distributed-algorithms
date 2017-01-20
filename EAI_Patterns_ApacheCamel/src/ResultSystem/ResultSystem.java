package ResultSystem;

import Order.Order;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import javax.jms.*;
import javax.jms.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *  the result system simply collects processed order and
 * prints a message to the console once a new order is received. The result
 * system must distinguish between valid and invalid orders
 * <p>
 */
public class ResultSystem implements Processor {

    private static final String TCP_LOCALHOST_61616 = "tcp://localhost:61616";

    private static final String NEW_ORDER = "NEW_ORDER";

    private static final String ITEM_STATUS = "ITEM_ORDER_STATUS";

    private static final String ITEM_AGGREGATION = "activemq:queue:ITEM_ORDER_AGGREGATION";

    private static final String ORDER_TOPIC = "ORDER";


    /**
     * The process method acts as the Message Translator from type Order to String
     */
    public void process(Exchange exchange) throws Exception {
        {
            org.apache.camel.Message msg = exchange.getIn();
            List<Order> aggregatedMainCheck = msg.getBody(ArrayList.class);

            Order order = null;
            Boolean valid = true;
            List<String> validationResults = new ArrayList<String>();

            for (Order o : aggregatedMainCheck) {
                // if not valid put result into list
                // set valid flag
                if (!o.getValid()) {
                    valid = false;
                    validationResults.add(o.getValidationResult());
                }

                order = o;
            }
            order.setValidationResult(validationResults.toString());
            order.setValid(valid);

            msg.setBody(order);
            msg.setHeader("type", "MainCheck");
            msg.setHeader("valid", valid);

        }
        exchange.getIn().removeHeader("type");
        exchange.getIn().removeHeader("orderID");
    }

    /**
     * Aggregation strategy for main
     */
    public static final class MainAggregationStrategy implements AggregationStrategy {

        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            if (oldExchange == null) {
                return newExchange;
            }

            List<Order> order = new ArrayList<Order>();
            order.add(oldExchange.getIn().getBody(Order.class));
            order.add(newExchange.getIn().getBody(Order.class));

            oldExchange.getIn().setBody(order);
            return oldExchange;
        }
    }

    /**
     * Starts the result system
     */
    public static void main(String[] args) {
        try {
            final ResultSystem orderConsumer = new ResultSystem();
            ActiveMQConnectionFactory conFactory = new ActiveMQConnectionFactory(TCP_LOCALHOST_61616);
            Connection con = conFactory.createConnection();

            // Create Camel Context
            DefaultCamelContext camelContext = new DefaultCamelContext();

            // Connect localhost ActiveMQ which should be separate process apache-activemq-5.14.3/bin$ ./activemq console
            ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent(TCP_LOCALHOST_61616);
            camelContext.addComponent("activemq", activeMQComponent);

            final Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue inQueue = session.createQueue(NEW_ORDER);
            Queue itemStatusQueue = session.createQueue(ITEM_STATUS);
            MessageConsumer consumer = session.createConsumer(inQueue);
            MessageConsumer itemStatusConsumer = session.createConsumer(itemStatusQueue);

            Topic topic = session.createTopic(ORDER_TOPIC);

            Queue itemAggregationQueue = session.createQueue("ITEM_ORDER_AGGREGATION");


            final MessageProducer producer = session.createProducer(topic);
            final MessageProducer itemStatusProducer = session.createProducer(itemAggregationQueue);

            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    try {
                        Order order = (Order) ((ObjectMessage) message).getObject();
                        String correlationID = UUID.randomUUID().toString();
                        order.setOrderID(correlationID);

                        Message enhancedOrder = session.createObjectMessage(order);
                        producer.send(enhancedOrder);
                    } catch (JMSException e1) {
                        e1.printStackTrace();
                    }
                }
            });

            itemStatusConsumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    try {
                        itemStatusProducer.send(message);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            camelContext.addRoutes(new RouteBuilder(camelContext) {
                public static final String VALID_ORDER = "direct:VALIDATED_ORDER";
                public static final String INVALID_ORDER = "direct:INVALID_ORDER";


                @Override
                public void configure() throws Exception {
                    from(ITEM_AGGREGATION)
                            // Aggregator for billing and inventory order
                            .aggregate(header("orderID"), new MainAggregationStrategy())
                            .completionPredicate(property(Exchange.AGGREGATED_SIZE)
                                    .isEqualTo(2))
                            .process(orderConsumer)

                            // Content Based Router for Validated or not
                            .choice()
                            .when(header("valid").isEqualTo("true"))
                            .to(VALID_ORDER)
                            .otherwise()
                            .to(INVALID_ORDER)
                            .end();


                    // Prints results
                    from(VALID_ORDER)
                            .process(new Processor() {
                                public void process(Exchange exchange) throws Exception {
                                    Order order = exchange.getIn().getBody(Order.class);
                                    System.out.println("\nValidated order: \n" + order.toString() + "\n");
                                }
                            })
                            .end();


                    from(INVALID_ORDER)
                            .process(new Processor() {
                                public void process(Exchange exchange) throws Exception {
                                    Order order = exchange.getIn().getBody(Order.class);
                                    System.out.println("\nInvalid order: \n" + order.toString() + "\n");
                                }
                            })
                            .end();
                }
            });

            con.start();
            camelContext.start();

            System.in.read();

            camelContext.stop();
            con.close();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
