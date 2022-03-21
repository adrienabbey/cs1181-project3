// Adrien Abbey, CS-1181L-07, Mar. 19, 2022
// Customer class for Project 3

public class Customer implements Comparable<Customer> {
    /* Fields */

    private static int customerCount = 0; // Track how many customers there are. Also used to determine customer number.
    private int customerNumber; // Assign customers an ID number
    private double arrivalTime; // Time of arrival at the store (in minutes relative from the store opening)
    private int orderSize; // Order size (the number of items they purchase)
    private double averageSelectionTime; // The average time it takes to select each item (total time spent browsing
                                         // divided by order size)
    private double endShoppingTime; // Time the customer finished filling their cart (in minutes relative to store
                                    // opening)
    private double waitingTime; // Time the customer waited to start checking out, if any.
    private double checkoutTime; // Time the customer finished checking out and left the store (in minutes
                                 // relative to the store opening)

    /* Constructor */

    public Customer(double arrivalTime, int orderSize, double averageSelectionTime) {
        customerCount++;
        this.customerNumber = customerCount;
        this.arrivalTime = arrivalTime;
        this.orderSize = orderSize;
        this.averageSelectionTime = averageSelectionTime;
    }

    /* Methods */

    public int getCustomerNumber() {
        return customerNumber;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public int getOrderSize() {
        return orderSize;
    }

    public double getAverageSelectionTime() {
        return averageSelectionTime;
    }

    public double getEndShoppingTime() {
        // This method not only returns the time the customer finished filling their
        // cart (relative to store opening), but also sets the customer's variable for
        // record tracking purposes.
        this.endShoppingTime = arrivalTime + (orderSize * averageSelectionTime);
        return endShoppingTime;
    }

    @Override
    public int compareTo(Customer other) {
        // We probably want to sort customers based on their arrival time.
        // Customers arriving sooner should be at the top of the PriorityQueue.

        // While I could order customers based on their arrival time, ultimately what
        // really matters is only what time they finish selecting items and are ready to
        // check out:

        // If this customer arrived later than the other:
        if (this.getEndShoppingTime() > other.getEndShoppingTime()) {
            // Then this customer arrived after the other:
            return 1;
        } else if (this.getEndShoppingTime() < other.getEndShoppingTime()) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Customer [arrivalTime=" + arrivalTime + ", averageSelectionTime=" + averageSelectionTime
                + ", checkoutTime=" + checkoutTime + ", customerNumber=" + customerNumber + ", endShoppingTime="
                + endShoppingTime + ", orderSize=" + orderSize + ", waitingTime=" + waitingTime + "]";
    }

}
