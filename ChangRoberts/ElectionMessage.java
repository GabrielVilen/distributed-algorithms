import teachnet.view.renderer.Shape;

import java.awt.*;
import java.util.Random;

public class ElectionMessage {
	Color color;
	int rand;
	Shape shape = Shape.RHOMBUS;
	int id = -1;
	String message;


	public ElectionMessage(int id) {
		message = "" + id;
		this.id = id;
		Random random = new Random();
		this.rand = random.nextInt();
		this.color = new Color(16 * rand);
	}

	@Override
	public String toString() {
		return "ElectionMessage{" +
				"message='" + message + '\'' +
				'}';
	}
}
