import java.awt.Color;
import teachnet.view.renderer.Shape;
import java.util.Random;

public class ElectedMessage {
	Color color;
	int rand;
	Shape shape = Shape.RHOMBUS;
	int id = -1;

	public ElectedMessage(int id) {
		this.id = id;
		Random random = new Random();
		this.rand = random.nextInt();
		this.color = new Color(16 * rand);
	}
}
