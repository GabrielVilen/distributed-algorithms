package Order;

import java.io.Serializable;

public class OrderItem implements Serializable {
	public String type; // type of the item
    public String orderID; // type of the item
    public int quantity; // how many we want
    public Order parent = null; // whether that many items can be ordered     
    public int available; // how many we can get      
    
    public OrderItem(String type, String orderID, int quantity, Order parent) {
        this.type = type;
        this.orderID = orderID;
        this.quantity = quantity;  
        this.parent = parent;  
        this.available = 0;
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "type='" + type + '\'' +
                ", orderID='" + orderID + '\'' +
                ", quantity='" + quantity + '\'' +
                ", available='" + available + '\'' +
                ", parent='" + parent.toString() + '\'' +
                '}';
    }
} 

