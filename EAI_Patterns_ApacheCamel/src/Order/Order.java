package Order;

import java.io.Serializable;

/**
 * Created by gabri on 2017-01-12.
 */
public class Order implements Serializable {
    private String CustomerID;
    private String FirstName;
    private String LastName;
    private int OverallItems; // (Number of all items in order)
    private int NumberOfDivingSuits;
    private int NumberOfSurfboards;
    private String OrderID;
    private String Valid;
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

    public String getValid() {
        return Valid;
    }

    public void setValid(String valid) {
        Valid = valid;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(String validationResult) {
        this.validationResult = validationResult;
    }

}
