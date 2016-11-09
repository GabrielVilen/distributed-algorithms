import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.Random;

public class PingPong extends BasicAlgorithm
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
		color = new Color(16 * rand.nextInt());
		
	}
	public void initiate()
	{
		int i;
		for (i = 0; i < checkInterfaces(); i++) {
			send(i, new TextMessage("PING"));
		}
	}
	public void receive(int interf, Object message)
	{
		if (message instanceof TextMessage) {
			TextMessage msg = (TextMessage) message;
			String text =  msg.getMessage();
			
			if(text.equals("PING")){
				TextMessage pong = new TextMessage("PONG");
				pong.setColor(msg.getColor());
				send((interf) % checkInterfaces(), pong );

			}else if (text.equals("PONG")){
					
				send((interf + 1) % checkInterfaces(), new TextMessage("PING"));
			}	
					
		}
	}
}
