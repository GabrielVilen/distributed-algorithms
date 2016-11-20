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
 * This class contains the implementation of the Echo algorithm. 
 * 
 * The algorithm works as following:
 * 	- The initiator node sends explorer messages to all its neighbors  
 * 	- When a node recieves an explorer it memorizes the node it got the message from (activator node) 
 * 	   and sends explorer to its neighbors
 *  - When a node has recieved an explorer over all its edges it sends a echo to its activator link.
 *  - The algorithm terminates when the last echo or explorer arrives
 *
 * 
 */
public class Echo extends BasicAlgorithm {
	final static int uninitialized = -1;
	final static int greenColor = 65280;
	final static int redColor = 16711680;

	Color color = null;
	int markInterface = uninitialized;
	String caption;
	int count = 0;
	Random rand = null;

	private int activator = uninitialized; // stores the ID of the node which send an explorer, uninitialized means not informed.
	private boolean isInitiator = false;
	private int id;

	public void setup(java.util.Map<String, Object> config) {
		this.id = (Integer) config.get("node.id");
		caption = "" + this.id;
		rand = getRandom();
		color = Color.gray;
	}

	// Only executed by initiator node
	public void initiate() {
		for (int i = 0; i < checkInterfaces(); i++) {
			send(i, new TextMessage("EXPLORER"));
		}
		this.isInitiator = true;
	}

	// Sends an ack, echo, to this node's activator node
	private void sendAckToActivator() {
		color = Color.green;
		send(this.activator, new TextMessage("ECHO"));
	}

	public void receive(int interf, Object message) {
		if (message instanceof TextMessage) {
			TextMessage msg = (TextMessage) message;
			String text = msg.getMessage();

			if (text.equals("EXPLORER")) {
				System.out.println("EXPLORER ARRIVED AT NODE " + caption);
				if (this.activator == uninitialized) {
					// Send explorer to all interfaces except activator
					for (int i = 0; i < checkInterfaces(); i++) {
						if (i != interf)
							send(i, new TextMessage("EXPLORER"));
					}
					color = Color.red;
					this.activator = interf;
				}
			}
			count++;
			if (count == checkInterfaces()) {
				if (!this.isInitiator) {
					// Node is not the initiator 
					sendAckToActivator();
				} else {
					// Node is the initiator
					color = Color.green;
				}
			}
		}
	}
}
