package Communication;

import java.io.IOException;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.Queue;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Message;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import InventorySystem.InventorySystem.InventoryAggregationStrategy;
import Order.Order;

/**
 * Created by mrow4a on 2017-01-17.
 */
public class CamelMain implements Processor {

    private static final String TCP_LOCALHOST_61616 = "tcp://localhost:61616";
    
    private static final String NEW_ORDER = "NEW_ORDER";

    private static final String ITEM_STATUS = "ITEM_ORDER_STATUS";
    
    private static final String ITEM_AGGREGATION = "activemq:queue:ITEM_ORDER_AGGREGATION";
    
    private static final String ORDER_TOPIC = "ORDER";
    

    /**
     * The process method acts as the Message Translator from type Order to String
     */
    public void process(Exchange exchange) throws Exception {
    	String validatedOrder = exchange.getIn().getBody(String.class);
    	String headers = exchange.getIn().getHeaders().toString();

	    System.out.println("Validated Order: " + headers + " " + validatedOrder);
    }
    
    public static void main(String[] args) {
        try {
            final CamelMain orderConsumer = new CamelMain();
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
				public void onMessage(Message message){
                    try {
						Order order = (Order)((ObjectMessage)message).getObject();
						String correlationID = UUID.randomUUID().toString();
						order.setOrderID(correlationID);

                        Message enhancedOrder = session.createObjectMessage(order);
				        producer.send(enhancedOrder);
				        System.out.println("Enhancing Order: " + enhancedOrder.toString());
					} catch (JMSException e1) {
						e1.printStackTrace();
					}
				}
            });
            
            itemStatusConsumer.setMessageListener(new MessageListener() {
				public void onMessage(Message message){
					try {
						itemStatusProducer.send(message);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
            });
            
            camelContext.addRoutes(new RouteBuilder(camelContext) {
                @Override
                public void configure() throws Exception {
                    from(ITEM_AGGREGATION)
                    .process(orderConsumer); // creates point-to-point channel
	                //TODO: Implement aggregation .aggregate(header("orderID"), new InventoryAggregationStrategy()).completionPredicate(property(Exchange.AGGREGATED_SIZE).isEqualTo(2))

	                //TODO: impement Content Based Router for Validated or not 
                    //.choice() 
                    //	.when(header("valid").isEqualTo("true"))
	                // 		.process(new Processor() {
					//			public void process(Exchange exchange) throws Exception {
					//			}
	               //  		})
                    //.to("direct:valid etc")
                    
                    // TODO: Print proper result messages
	                // from("direct:valid etc")
	                // .process(new Processor() {
					//	public void process(Exchange exchange) throws Exception {
					//	}
	               //  })
	                // .end();
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
