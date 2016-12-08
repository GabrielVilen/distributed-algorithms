import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.Random;

/**
 * Distributed Algorithms
 * <p>
 * Homework 1
 * <p>
 * Group G10
 * <p>
 * Piotr Mrowczynski 387521
 * Gabriel Vilen 387555
 * Stefan Stojkovski 387529
 * Tong Li 387568
 * <p>
 * This class contains the implementation of the Chang Roberts algorithm.
 * <p>
 * The algorithm works as following: - TODO
 */
public class ChangRoberts extends BasicAlgorithm {
    Color color;
    String caption;

    private Random rand;
    private boolean isParticipant = false, isLeader = false;
    private int id, electedId=0;

    public void setup(java.util.Map<String, Object> config) {
        this.id = (Integer) config.get("node.id");
        caption = "" + this.id;
        rand = getRandom();
        color = Color.gray;
    }

    // Only executed by initiator node
    public void initiate() {
    	sendToNeighbour(id, new ElectionMessage(id, false)); // send this node's id to clockwise neighbor
    }

    // sends message to clockwise neighbour
    private void sendToNeighbour(int interf, ElectionMessage message) {
        int reciever = (interf + 1) % checkInterfaces();
        send(reciever, message);
    }
        
    public void receive(int interf, Object msg) {
        ElectionMessage message = (ElectionMessage) msg;
        if (!message.isElected) {
            int cmpId = message.id;

            if (cmpId > id) {
                sendToNeighbour(interf, message); // forwards msg to neighbour
            } else if (cmpId < id) {
                message.id = id;    // updates msg id to this larger id
                sendToNeighbour(interf, message);
            } else {
                color = Color.blue;
                electedId = id;
                sendToNeighbour(interf, new ElectionMessage(id, true));
            }
        } else { // second round, check for elected msg
        	if (electedId == message.id){
        		return;
        	}
            electedId = message.id;
            System.out.println(electedId +" " + message.id);
            caption = "" + this.id + " elected: " + electedId;
            color = Color.green;
            sendToNeighbour(interf, message);
        }
    }
}
