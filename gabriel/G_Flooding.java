import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.Random;

public class G_Flooding extends BasicAlgorithm {

	boolean isInitiator = false, isInformed = false;
	String confirmation = "Confirmation", explorer = "Explorer";
	String caption;
	Random rand;
	Color color;
	int count = 0,
	int a;

	@Override
	public void setup(java.util.Map<String, Object> config) {
		int id = (int) config.get("node.id");
		isInitiator = true;
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
		a = n;
		color = Color.GREEN;

		send(a, confirmation);
	}

	// TODO: idea: if higher seq (id) number found, update this seq nr and flood
	// (= new msg found).
	// else don't flood (already flooded msg)
	@Override
	public void receive(int n, Object message) {
		// {Explorer from neighbor N is received}
		if ((String) message == explorer) {

			if (a != n) {				
				for (int i = 0; i < checkInterfaces(); i++)
					if (i != n) {
						send(i, explorer);
					}
				// inform();
				a = n;
			}
//			else if(!isInformed && checkInterfaces() == 1) { // leaf
//				a = n;
//			}

			else {
				send(n, confirmation);
			}
		}

		// {Confirmation is received}
		if ((String) message == confirmation) {
			count++;
			System.out.println("ID: " + id + " count: " + count + " interfaces: " + checkInterfaces());

			if (!isInitiator && (count == checkInterfaces() - 1)) {
				//send(a, confirmation);
				inform(a);
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
