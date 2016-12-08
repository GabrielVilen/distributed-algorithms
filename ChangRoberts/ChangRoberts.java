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
 * This class contains the implementation of the Chang Roberts algorithm.
 * 
 * The algorithm works as following: - TODO
 *
 * 
 */
public class ChangRoberts extends BasicAlgorithm {
	private final static int GREEN = 65280, RED = 16711680;

	Color color;
	String caption;

	private Random rand;
	private boolean isParticipant = false, isLeader = false;
	private int id, electedId;

	public void setup(java.util.Map<String, Object> config) {
		this.id = (Integer) config.get("node.id");
		caption = "" + this.id;
		rand = getRandom();
		color = Color.gray;
	}

	// Only executed by initiator node
	public void initiate() {
		sendToNeighbour(id, new ElectionMessage(id)); // send this node's id to clockwise neighbor
		isParticipant = true;
	}

	// sends message to clockwise neighbour
	private void sendToNeighbour(int interf, Object message) {
		// color = Color.green;
		send(interf++ % checkInterfaces(), message);
		isParticipant = true;
	}

	public void receive(int interf, Object message) {
		if (message instanceof ElectionMessage) {
			int cmpId = message.id;

			switch (cmpId) {
			case cmpId > id:

				sendToNeighbour(interf, message); // forwards msg to neighbour
				break;

			case cmpId < id && !isParticipant:

				message.id = id; 	// updates msg id to this larger id
				sendToNeighbour(interf, message);
				break;

			case cmpId == id:

				isLeader = true; 	// elect this node as leader
				isParticipant = false;
				sendToNeighbour(id, new ElectedMessage(id));
				break;
			}
		} else if (message instanceof ElectedMessage) { // second round, check for elected msg
			isParticipant = false;
			electedId = message.id;
			sendToNeighbour(intef, message);

			if (isLeader) { 		// leader recieve his own elected-msg, election is over
				color = GREEN;
				exit();
			}

		}
	}
}
