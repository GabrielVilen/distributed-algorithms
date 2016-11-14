import java.awt.Color;
import teachnet.view.renderer.Shape;
import java.util.Random;

public class TextMessage {
	Color color;
	int rand;
	Shape shape = Shape.RHOMBUS;
	String message = "";

	public TextMessage(String msg) {
		this.message = msg;
		Random random = new Random();
		this.rand = random.nextInt();
		this.color = new Color(16 * rand);
	}

	public int getInt() {
		return rand;
	}
	public String getMessage(){
		return this.message;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String toString() {
		return this.message;
	}
}
