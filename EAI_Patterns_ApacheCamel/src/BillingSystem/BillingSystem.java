package BillingSystem;

import java.util.Random;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import Order.Order;
/**
 * Created by mrow4a on 2017-01-12.
 */
public class BillingSystem implements Processor {

    private static final String TCP_LOCALHOST_61616 = "tcp://localhost:61616";

    private static final String ORDER_TOPIC = "activemq:topic:ORDER?clientId=BILLING&durableSubscriptionName=BILLING";
    
    private static final String ITEM_STATUS = "activemq:queue:ITEM_ORDER_STATUS";
    
    private static final int surfboardPrice = 100;
    
    private static final int divingsuitPrice = 200;
    
  	public void process(Exchange exchange) throws Exception {
	     Order order = exchange.getIn().getBody(Order.class);
	     
	     // Calculate amount of money required for shopping
	     int totalRequired = divingsuitPrice*order.getNumberOfDivingSuits() + surfboardPrice*order.getNumberOfSurfboards();

	     // Give Customer 25% chance that he has enough money on the account for the selected items 
	     // by generating random number in range 0 to totalRequired*4
	     Random rn = new Random();
         int customerAmountOfMoney = rn.nextInt(totalRequired*4);
	     
	     if (customerAmountOfMoney >= totalRequired) {
	    	 order.setValid(true);
	     } else {
	    	 order.setValid(false);
	    	 order.setValidationResult("Not enough money. Required: " + totalRequired);
	     }
	     
	     exchange.getIn().setBody(order);
	     exchange.getIn().setHeader("type", "BillingCheck");
	     exchange.getIn().setHeader("orderID", order.getOrderID());
	     System.out.println("BillingSystem: " + order.toString());
  	}
  	

	public static void main(String[] args) {
	     try {
	         final BillingSystem billingTopicConsumer = new BillingSystem();
	
	         // Create Camel Context
	         DefaultCamelContext camelContext = new DefaultCamelContext();
	
	         // Connect localhost ActiveMQ which should be separate process apache-activemq-5.14.3/bin$ ./activemq console
	         ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent(TCP_LOCALHOST_61616);
	         camelContext.addComponent("activemq", activeMQComponent);
	         camelContext.addRoutes(new RouteBuilder(camelContext) {
	             @Override
	             public void configure() throws Exception {
	                 from(ORDER_TOPIC)
	                 // InventoryCheck Splitter
	                 .process(billingTopicConsumer)
	                 .to(ITEM_STATUS)
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
}

