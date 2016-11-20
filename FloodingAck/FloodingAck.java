import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.Random;

/**
 * Distributed Algorithms 
 * 
 * Homework 1
 *  
 * Group G10
 * 
 * Piotr Mrowczynski 387521 
 * Gabriel Vilén 387555 
 * Stefan Stojkovski 387529 
 * Tong Li 387568
 * 
 * This class contains the implementation of the Flooding with acknowledgment algorithm. 
 * 
 * The algorithm works as following:
 * 	- The initiator node sends explorer messages to all its neighbors  
 * 	- A node acks an explorer with a confirmation as soon as it has recieved a confirmation for
 * 	  all explorer send by itself
 *  - The algorithm terminates if the initiator recieved a confirmation from every neighbor
 *
 * 
 */
public class FloodingAck extends BasicAlgorithm
{
	final static int uninitialized = -1;
	
	Color color = null;
	int markInterface = uninitialized;
	String caption;
	int count = 0;
	Random rand = null;
	
	private int activator = uninitialized;  // stores the ID of the node which send an explorer, uninitialized means not informed.
	private boolean isInitiator = false;
	private int id;
	
	public void setup(java.util.Map<String, Object> config)
	{
		this.id = (Integer) config.get("node.id");
		caption = "" + this.id;
		rand = getRandom();
		color = Color.gray;
	}
	

	// Only executed by initiator node
	public void initiate()
	{
		for (int i = 0; i < checkInterfaces(); i++) {
			send(i, new TextMessage("EXPLORER"));
		}
		this.isInitiator = true;
	}
	

	// Sends an ack to this node's activator node, activator
	private void sendAckToActivator() {
		color = Color.green;
		send(this.activator, new TextMessage("ACK"));
	}
	
	public void receive(int interf, Object message)
	{
		if (message instanceof TextMessage) {
			TextMessage msg = (TextMessage) message;
			String text =  msg.getMessage();
			
			if(text.equals("EXPLORER")){
				if (this.activator == uninitialized) {
					// Send explorer to all interfaces except activator
					for (int i = 0; i < checkInterfaces(); i++) {
						if (i != interf)
							send(i, new TextMessage("EXPLORER"));
					}
					color = Color.red;
					this.activator = interf;
					
					// Node is leaf
					if (checkInterfaces() == 1){
						sendAckToActivator();
					}
					
				} else {					
					// Node will send confirmation to activator immedietaly since it has only one link
					color = Color.green;
					send(interf, new TextMessage("ACK"));
				}
			}else if (text.equals("ACK")){
				count++;
				if ((!this.isInitiator) && (count == checkInterfaces()-1)) {
					// Node is not initiator and cound=#neighbors-1
					System.out.println("NODE "+caption+" RECEIVED COUNT "+count);
					sendAckToActivator();
				} else if (this.isInitiator && (count == checkInterfaces())){
					// Node is initiator and cound=#neighbors, exit!
					System.out.println("NODE "+caption+" RECEIVED COUNT "+count);
					color = Color.green;
				}
			}				
		}
	}
}
