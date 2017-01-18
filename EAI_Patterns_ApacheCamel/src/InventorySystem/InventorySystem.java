package InventorySystem;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import Order.Order;

/**
 * Created by mrow4a on 2017-01-12.
 */
public class InventorySystem implements Processor{

    private static final String TCP_LOCALHOST_61616 = "tcp://localhost:61616";

    private static final String ORDER_TOPIC = "activemq:topic:ORDER?clientId=INVENTORY&durableSubscriptionName=INVENTORY";
        
  /**
  * The process method acts as the Message Translator from type Order to String
  */
	public void process(Exchange exchange) throws Exception {
	     Message message = exchange.getIn();
	     OrderItems object = new OrderItems();
	     
	     Order order = message.getBody(Order.class);
	     
	     if (order.getNumberOfDivingSuits() > 0){
	    	 object.items.add(
	    			 new OrderItem("DivingSuit", order.getOrderID(),order.getNumberOfDivingSuits(), order));
	     }

	     if (order.getNumberOfSurfboards() > 0){
	    	 object.items.add(
	    			 new OrderItem("Surfboard", order.getOrderID(), order.getNumberOfSurfboards(), order));
	     }
	     
	     message.setBody(object);
	}

    public static boolean isSurfboard(@Body OrderItem orderItem) {
        return orderItem.type.equals("Surfboard");
    }
	
    public class OrderItems {
        List<OrderItem> items = new ArrayList<OrderItem>();

        public List<OrderItem> getItems() {
            return items;
        }

        public void setItems(List<OrderItem> items) {
            this.items = items;
        }
    }   
    
    public class OrderItem implements Serializable {
		public String type; // type of the item
        public String orderID; // type of the item
        public int quantity; // how many we want
        public Order parent; // whether that many items can be ordered     
        public int available; // how many we can get      
        
        public OrderItem(String type, String orderID, int quantity, Order parent) {
            this.type = type;
            this.orderID = orderID;
            this.quantity = quantity;  
            this.parent = parent;  
            this.available = 0;
        }
        
        @Override
        public String toString() {
            return "OrderItem{" +
                    "type='" + type + '\'' +
                    ", orderID='" + orderID + '\'' +
                    ", quantity='" + quantity + '\'' +
                    ", available='" + available + '\'' +
                    ", parent='" + parent.toString() + '\'' +
                    '}';
        }
    } 
    
    private static int validateOrder(Message message, int stockValue) {
	     OrderItem order = message.getBody(OrderItem.class);
	     
	     int required = order.quantity;
	     int available = 0;
	     if (required <= stockValue){
	    	 stockValue -= required;
		     available = required;
		     message.setHeader("valid", true);
	     } else {
	    	 available = stockValue;
		     message.setHeader("valid", false);
	     }
	     
	     order.available = available;
	     message.setBody(order);
	     System.out.println("InventorySystem: " + order.toString());
	     return stockValue;
    }
    
    private static int surfboardsCount = 25;
    private static int diveSuitCount = 25;
    
	public static void main(String[] args) {
	     try {
	         final InventorySystem inventoryTopicConsumer = new InventorySystem();
	
	         // Create Camel Context
	         DefaultCamelContext camelContext = new DefaultCamelContext();
	
	         // Connect localhost ActiveMQ which should be separate process apache-activemq-5.14.3/bin$ ./activemq console
	         ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent(TCP_LOCALHOST_61616);
	         camelContext.addComponent("activemq", activeMQComponent);
	         camelContext.addRoutes(new RouteBuilder(camelContext) {
	             @Override
	             public void configure() throws Exception {
	                 from(ORDER_TOPIC)
	                 .process(inventoryTopicConsumer)
	                 .split(simple("${body.items}"))
	                 .process(new Processor() {
						public void process(Exchange exchange) throws Exception {
						     Message message = exchange.getIn();
						     OrderItem orderItem = message.getBody(OrderItem.class);
						     message.setHeader("type", orderItem.type);
						     message.setHeader("orderID", orderItem.orderID);
						}
	                 })
	                 .choice() 
                     	.when(header("type").isEqualTo("Surfboard"))
                     		.to("direct:surfboardsInventory")
                     	.when(header("type").isEqualTo("DivingSuit"))
                     		.to("direct:divingSuitsInventory")
                     	.otherwise()
                 			.to("direct:invalidOrder")
                    .end();
	                 

	                 from("direct:surfboardsInventory")
	                 .process(new Processor() {
						public void process(Exchange exchange) throws Exception {
						     Message message = exchange.getIn();
						     
						     surfboardsCount = validateOrder(message, surfboardsCount);
						}
	                 })
	                 .end();
	                 

	                 from("direct:divingSuitsInventory")
	                 .process(new Processor() {
						public void process(Exchange exchange) throws Exception {
						     Message message = exchange.getIn();
						     OrderItem order = message.getBody(OrderItem.class);
						     
						     diveSuitCount = validateOrder(message, diveSuitCount);
						}
	                 })
	                 .end();
	                 

	                 from("direct:invalidOrder")
	                 .process(new Processor() {
						public void process(Exchange exchange) throws Exception {
						     Message message = exchange.getIn();

						     message.setHeader("valid", false);
						     System.out.println("InventorySystem direct:invalidOrder: " + message.toString());
						}
	                 })
	                 .end();
	             }
	         });
	         camelContext.start();
	
	         System.in.read(); // wait till ENTER pressed
	
	         camelContext.stop();
	
	     } catch (Exception e) {
	         e.printStackTrace();
	     }
	}
 
//public static void main(String[] args) throws JMSException {
//// Getting JMS connection from the server
//
//try {
//   ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(TCP_LOCALHOST_61616);
//   Connection con = connectionFactory.createConnection();
//
//   con.setClientID(CLIENT);
//   
//   Session session = con.createSession(false,
//           Session.AUTO_ACKNOWLEDGE);
//
//   Topic topic = session.createTopic(ORDER_TOPIC);
//
//   MessageConsumer consumer = session.createDurableSubscriber(topic, CLIENT);
//
//   MessageListener listner = new MessageListener() {
//       public void onMessage(Message message) {
//           System.out.println("onMessage: " + message.toString());
//       }
//   };
//
//   consumer.setMessageListener(listner);
//   
//   con.start();
//	System.in.read();
//   con.close();
//} catch (IOException e) {
//	e.printStackTrace();
//}
//}
//
 
}
