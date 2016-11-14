import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.Random;
/**
 I: {NOT informed} // Executed by Initiator
		SEND Explorer TO all Neighbors;
		informed := TRUE;

{Explorer from neighbor N is received}
	IF NOT informed THEN
		SEND Explorer TO all Neighbors except N;
		informed := TRUE;
		A := N;
	ELSE
		SEND Confirmation TO N;
	FI
	
{Confirmation is received}
	Count := Count + 1;
	IF (NOT Initiator) AND (Count == #Neighbors – 1) THEN
		SEND Confirmation TO Neighbor A;
	FI
	IF Initiator AND (Count == #Neighbors) THEN
		Exit; // Algorithm is terminated.
	FI
 
 */
public class G_Flooding extends BasicAlgorithm {
	
	boolean isInitiator = false, isInformed = false;
	String confirmation = "Confirmation", explorer = "Explorer";
	String caption;
	Random rand;
	Color color;
	int count = 0;
	private int a;

	@Override
	public void setup(java.util.Map<String, Object> config) {
		int id = (int) config.get("node.id");
		if (id == 0) {
			isInitiator = true;
			//color = Color.YELLOW;
		}
		color = Color.RED;
		caption = "" + id;
	}

	// I: {NOT inform}
	@Override
	public void initiate() {
		if (isInitiator && !isInformed) {
			for (int i = 0; i < checkInterfaces(); i++) {
				send(i, explorer);
			}
			isInformed = true;
		}

	}
	
	private void inform() {
		System.out.println(id + " is informed!");
		isInformed = true;
		color = Color.GREEN;
		
	}

	@Override
	public void receive(int n, Object message) {
		// {Explorer from neighbor N is received}
		if ((String) message == explorer) {

			if (!isInformed) {
				for (int i = 0; i < checkInterfaces(); i++)
					if (i != n) {
						send(i, explorer);
					}
				inform();
				a = n;
			} else {
				send(n, confirmation);
			}
		}

		// {Confirmation is received}
		if ((String) message == confirmation) {
			count++;

			if (!isInitiator && (count == checkInterfaces() - 1)) {
				send(a, confirmation);
				//inform();
			} 
			if (isInitiator && (count == checkInterfaces())) {
				inform();				
				exit();
			}
		}

	}

	private void exit() {
		// TODO Auto-generated method stub

	}
}
