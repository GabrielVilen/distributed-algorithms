import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.Random;

public class FloodingAck extends BasicAlgorithm
{
	final static int uninitialized = -1;
	final static int greenColor = 65280;
	final static int redColor = 16711680;
	
	Color color = null;
	int markInterface = uninitialized;
	String caption;
	int count = 0;
	Random rand = null;
	
	// informedBy stores ID of node which send an explorer. 
	// [-1] means not informed. [id] means that node is initiator
	private int informedBy = uninitialized;
	private int id;
	
	public void setup(java.util.Map<String, Object> config)
	{
		this.id = (Integer) config.get("node.id");
		caption = "" + this.id;
		rand = getRandom();
		color = new Color(redColor);
	}
	
	public void initiate()
	{
		for (int i = 0; i < checkInterfaces(); i++) {
			send(i, new TextMessage("EXPLORER"));
		}
		this.informedBy = this.id;
	}
	
	private void confirmInformedBy() {
		//node informed by node informedBy, send ack on that interface
		color = new Color(greenColor);
		send(this.informedBy, new TextMessage("ACK"));
	}
	
	public void receive(int interf, Object message)
	{
		if (message instanceof TextMessage) {
			TextMessage msg = (TextMessage) message;
			String text =  msg.getMessage();
			
			if(text.equals("EXPLORER")){
				if (this.informedBy == uninitialized && checkInterfaces()>1) {
					//send explorer to all interfaces except an activator
					for (int i = 0; i < checkInterfaces(); i++) {
						if (i != interf)
							send(i, new TextMessage("EXPLORER"));
					}
					this.informedBy = interf;
				} else {					
					//it will send confirmation to activator immedietaly since it has only one interface
					if (this.informedBy == uninitialized && checkInterfaces()==1) {
						this.informedBy = interf;
					}
					confirmInformedBy();
				}
			}else if (text.equals("ACK")){
				count++;
				if ((this.informedBy != this.id) && (count == checkInterfaces()-1)) {
					// it is not an initiator node and cound=#neighbors-1
					confirmInformedBy();
				} else if ((this.informedBy == this.id) && (count == checkInterfaces())){
					// it i initiator and cound=#neighbors, exit!
					color = new Color(greenColor);
				}
			}				
		}
	}
}
