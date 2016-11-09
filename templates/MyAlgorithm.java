import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.Random;

public class MyAlgorithm extends BasicAlgorithm
{
	Color color = null;
	int markInterface = -1;
	String caption;
	Random rand = null;

	public void setup(java.util.Map<String, Object> config)
	{
		int id = (Integer) config.get("node.id");
		caption = "" + id;
		rand = getRandom();
	}
	public void initiate()
	{
		int i;
		for (i = 0; i < checkInterfaces(); ++i) {
			send(i, true);
			send(i, new MyMsg(rand.nextInt()));
		}
	}
	public void receive(int interf, Object message)
	{
		if (message instanceof Boolean) {
			send(interf, !(Boolean)message);
			return;
		} else if (message instanceof MyMsg) {
			MyMsg msg = (MyMsg) message;
			color = msg.getColor();
			caption = "Got " + message;
			markInterface = interf;
			send((interf + 1) % checkInterfaces(), new MyMsg(1 + msg.getInt()));
		}
	}
}
