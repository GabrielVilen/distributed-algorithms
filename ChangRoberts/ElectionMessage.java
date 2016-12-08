import java.awt.Color;
import teachnet.view.renderer.Shape;
import java.util.Random;

public class ElectionMessage {
	Color color;
	int rand;
	Shape shape = Shape.RHOMBUS;
	int id = -1;

	public ElectionMessage(int id) {
		this.id = id;
		Random random = new Random();
		this.rand = random.nextInt();
		this.color = new Color(16 * rand);
	}
}
