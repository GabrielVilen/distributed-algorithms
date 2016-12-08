import teachnet.view.renderer.Shape;

import java.awt.*;
import java.util.Random;

public class ElectionMessage {
	Color color;
	int rand;
	Shape shape = Shape.RHOMBUS;
	int id = -1;
	boolean isElected = false;
	String message;

	public ElectionMessage(int id, boolean isElected) {
		message = "" + id;
		this.id = id;
		Random random = new Random();
		this.rand = random.nextInt();
		this.color = new Color(16 * rand);
		this.isElected = isElected;
	}
	
	@Override
	public String toString() {
		return "ElectionMessage{" +
				"message='" + id + '\'' +
				'}';
	}
}
