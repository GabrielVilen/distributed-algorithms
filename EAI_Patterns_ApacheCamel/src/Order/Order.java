package Order;

import java.io.Serializable;

/**
 * The   result   of   the   order   application   integration   is   a   common/transformed
 * format for orders. Each order consists of following properties (type String):
 * ◦ CustomerID
 * ◦ FirstName
 * ◦ LastName
 * ◦ OverallItems (Number of all items in order)
 * ◦ NumberOfDivingSuits
 * ◦ NumberOfSurfboards
 * ◦ OrderID
 * ◦ Valid
 * ◦ validationResult
 * <p>
 */
public class Order implements Serializable {
    private String CustomerID;
    private String FirstName;
    private String LastName;
    private int OverallItems; // (Number of all items in order)
    private int NumberOfDivingSuits;
    private int NumberOfSurfboards;
    private String OrderID;
    private Boolean Valid;
    private String validationResult;


    @Override
    public String toString() {
        return "Order{" +
                "CustomerID='" + CustomerID + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", OverallItems='" + OverallItems + '\'' +
                ", NumberOfDivingSuits='" + NumberOfDivingSuits + '\'' +
                ", NumberOfSurfboards='" + NumberOfSurfboards + '\'' +
                ", OrderID='" + OrderID + '\'' +
                ", Valid='" + Valid + '\'' +
                ", validationResult='" + validationResult + '\'' +
                '}';
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public int getOverallItems() {
        return OverallItems;
    }

    public void setOverallItems(int overallItems) {
        OverallItems = overallItems;
    }

    public int getNumberOfDivingSuits() {
        return NumberOfDivingSuits;
    }

    public void setNumberOfDivingSuits(int numberOfDivingSuits) {
        NumberOfDivingSuits = numberOfDivingSuits;
    }

    public int getNumberOfSurfboards() {
        return NumberOfSurfboards;
    }

    public void setNumberOfSurfboards(int numberOfSurfboards) {
        NumberOfSurfboards = numberOfSurfboards;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public Boolean getValid() {
        return Valid;
    }

    public void setValid(Boolean valid) {
        Valid = valid;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(String validationResult) {
        this.validationResult = validationResult;
    }

}
