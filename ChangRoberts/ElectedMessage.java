import teachnet.view.renderer.Shape;

import java.awt.*;
import java.util.Random;

public class ElectedMessage {
	String message;
	Color color;
	int rand;
	Shape shape = Shape.RHOMBUS;
	int id = -1;

	public ElectedMessage(int id) {
		message = "" + id;
		this.id = id;
		Random random = new Random();
		this.rand = random.nextInt();
		this.color = new Color(16 * rand);
	}

	@Override
	public String toString() {
		return "ElectedMessage{" +
				"message='" + message + '\'' +
				'}';
	}
}
