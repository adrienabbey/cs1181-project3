// Adrien Abbey, CS-1181L-07, Mar. 19, 2022
// Customer class for Project 3

public class Customer implements Comparable<Customer> {
    /* Fields */

    private double arrivalTime; // Time of arrival at the store (in minutes relative from the store opening)
    private int orderSize; // Order size (the number of items they purchase)
    private double averageSelectionTime; // The average time it takes to select each item (total time spent browsing
                                         // divided by order size)

    /* Constructor */

    public Customer(double arrivalTime, int orderSize, double averageSelectionTime) {
        this.arrivalTime = arrivalTime;
        this.orderSize = orderSize;
        this.averageSelectionTime = averageSelectionTime;
    }

    /* Methods */

    public double getArrivalTime() {
        return arrivalTime;
    }

    public int getOrderSize() {
        return orderSize;
    }

    public double getAverageSelectionTime() {
        return averageSelectionTime;
    }

    public double shoppingTime() {
        // Return the time spent shopping
        return orderSize * averageSelectionTime;
    }

    @Override
    public int compareTo(Customer other) {
        // We probably want to sort customers based on their arrival time.
        // Customers arriving sooner should be at the top of the PriorityQueue.

        // FIXME: Test this method for correctness.

        // If this customer arrived later than the other:
        if (this.arrivalTime > other.arrivalTime) {
            // Then this customer arrived after the other:
            return 1;
        } else if (this.arrivalTime < other.arrivalTime) {
            return -1;
        } else {
            return 0;
        }
    }

}
