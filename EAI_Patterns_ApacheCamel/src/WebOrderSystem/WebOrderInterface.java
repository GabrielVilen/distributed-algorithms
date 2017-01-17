package WebOrderSystem;

/**
 * Created by gabri on 2017-01-17.
 */
public interface WebOrderInterface {

    /**
     * Acts as the public API method for adding new orders through the web interface
     */
    public void addOrder(Order.Order order);

}
