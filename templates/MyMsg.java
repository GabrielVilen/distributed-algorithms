import java.awt.Color;
import teachnet.view.renderer.Shape;

public class MyMsg {
	Color color;
	int rand;
	Shape shape = Shape.RHOMBUS;
	static int counter = 0;
	int id;

	public MyMsg(int rand) {
		this.rand = rand;
		this.color = new Color(16 * rand);
		this.counter++;
		id = counter;
	}

	public int getInt() {
		return rand;
	}
	
	public Color getColor() {
		return color;
	}

	public String toString() {
		return "#" + id;
	}
}