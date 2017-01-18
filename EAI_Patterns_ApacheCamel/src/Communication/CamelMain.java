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

import Order.Order;

/**
 * Created by mrow4a on 2017-01-17.
 */
public class CamelMain {

    private static final String TCP_LOCALHOST_61616 = "tcp://localhost:61616";
    
    private static final String NEW_ORDER = "NEW_ORDER";

    private static final String ORDER_TOPIC = "ORDER";
    
    public static void main(String[] args) {
        try {
            ActiveMQConnectionFactory conFactory = new ActiveMQConnectionFactory(TCP_LOCALHOST_61616);
            Connection con = conFactory.createConnection();

            final Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue inQueue = session.createQueue(NEW_ORDER);
            MessageConsumer consumer = session.createConsumer(inQueue);
            
            Topic topic = session.createTopic(ORDER_TOPIC);

            final MessageProducer producer = session.createProducer(topic);
            
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

            con.start();
            System.in.read();
            con.close();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
