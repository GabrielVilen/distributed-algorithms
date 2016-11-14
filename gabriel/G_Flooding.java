import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.Random;

public class G_Flooding extends BasicAlgorithm {
	boolean isInitiator = false, isInformed = false;
	char confirmation = 'C', explorer = 'E';
	String caption;
	Random rand;
	Color color;
	int count, id;

	@Override
	public void setup(java.util.Map<String, Object> config) {
		id = (int) config.get("node.id");
		if (id == 0) {
			isInitiator = true;
			color = Color.YELLOW;
		} else {
			color = Color.RED;
		}
		caption = "" + id;
	}
	/*
	 * I: {NOT informed} Executed by Initiator SEND Explorer TO all Neighbors;
	 * informed := TRUE;
	 */
	@Override
	public void initiate() {
		if (isInitiator && !isInformed) {
			for (int i = 0; i < neighbors.length; i++) {
				send(i, explorer);
			}
			isInformed = true;
		}

	}

	@Override
	public void receive(int n, Object message) {
		/*
		 * {Explorer from neighbor N is received} 
		 * 
		 * IF NOT informed THEN SEND Explorer
		 * 		TO all Neighbors except N; 
		 * 		informed := TRUE; 
		 * 		A := N;
		 *  ELSE SEND
		 * 		Confirmation TO N; 
		 *  FI
		 */
		if((char)message == explorer) {

			if (!isInformed) {
				for (int i = 0; i < checkInterfaces(); i++)
					if (i != n) {
						send(i, explorer);
					}
			}
			isInformed = true;
			a = n;
			} else {
				send(n, confirmation);
			}

		/*
		 * {Confirmation is received}
			Count := Count + 1;
			IF (NOT Initiator) AND (Count == #Neighbors – 1) THEN
				SEND Confirmation TO Neighbor A;
			FI
			IF Initiator AND (Count == #Neighbors) THEN
				Exit; // Algorithm is terminated.
			FI			
		 */
		else if((char)message == confirmation) {
			count++;
			
			if(!isInitiator && (count == checkInterfaces() - 1)) {
				send(a, confirmation);
			} else if(isInitiator && (count == checkInterfaces())) {
				color = Color.GREEN;
				exit();
			}
		}
	}

	private void exit() {
		// TODO Auto-generated method stub

	}
}
