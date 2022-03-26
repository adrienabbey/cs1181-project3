// Adrien Abbey, CS-1181L-07, Mar. 19, 2022
// Customer class for Project 3

public class Customer implements Comparable<Customer> {
    /* Fields */

    private static int customerCount = 0; // Track how many customers there are. Also used to determine customer number.
    private int customerNumber; // Assign customers an ID number
    private double arrivalTime; // Time of arrival at the store (in minutes relative from the store opening)
    private int orderSize; // Order size (the number of items they purchase)
    private double averageSelectionDuration; // The average time it takes to select each item (total time spent browsing
                                             // divided by order size)
    private double endShoppingTime; // Time the customer finished filling their cart (in minutes relative to store
                                    // opening)
    private Double waitDuration; // Time the customer waited to start checking out, if any.
    private double checkoutDuration; // How long it took the customer to scan their items and pay for them
    private double finishTime; // Time the customer finished all their shopping and left the store (time
                               // relative to store opening)
    private int status; // Tracks what the customer's status is. See getStatus() method for details.
    private Checkout checkoutLane; // Tracks what lane the customer is in, if any.

    /* Constructor */

    public Customer(double arrivalTime, int orderSize, double averageSelectionTime) {
        customerCount++;
        this.customerNumber = customerCount;
        this.arrivalTime = arrivalTime;
        this.orderSize = orderSize;
        this.averageSelectionDuration = averageSelectionTime;
        this.status = 0; // When first added to the event queue (loading of customer data), their status
                         // is 0, meaning they haven't entered the store yet.
        this.checkoutLane = null; // Customers don't have a checkout lane at first.
        this.waitDuration = null; // Customers don't have a wait duration at first. Null will hopefully help me
                                  // error check issues.
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

    public double getAverageSelectionDuration() {
        return averageSelectionDuration;
    }

    public Checkout getCheckoutLane() {
        return checkoutLane;
    }

    public int getStatus() {
        // Returns the customer's current status:
        // 0: customer hasn't entered the store yet
        // 1: customer is currently selecting items and filling their cart
        // 2: customer finished filling their cart and is waiting in line to checkout
        // 3: customer has started checking out
        // 4: customer has finished checking out
        // null: something went horribly wrong

        return status;
    }

    public double getEndShoppingTime() {
        // Returns the time the customer finishes filling their cart (relative to store
        // opening):
        return arrivalTime + (orderSize * averageSelectionDuration);
    }

    public double getEndCheckoutTime() {
        // Return the time the customer finished checking out (relative to store
        // opening):
        return arrivalTime + (orderSize * averageSelectionDuration) + waitDuration + checkoutDuration;
    }

    public double getEventTime() {
        // Looks at the customer's current status and calculates when the time of their
        // next event.

        Double returnTime = null;

        // When customers are first loaded, their status is 0: they have not arrived
        // yet. Return their arrival time:
        if (status == 0) {
            returnTime = arrivalTime;
        }

        // If their status is 1, they've started shopping: return the time when they'll
        // finish filling their cart:
        if (status == 1) {
            returnTime = arrivalTime + (orderSize * averageSelectionDuration);
        }

        // If their status is 2, they've finished filling their cart and are waiting on
        // others in their checkout lane:
        if (status == 2) {
            returnTime = arrivalTime + (orderSize * averageSelectionDuration) + waitDuration;
        }

        // If their status is 3 or 4, they're currently checking out (or finished
        // checking out). Return when they'll finish (or did finish):
        if (status == 3 || status == 4) {
            returnTime = arrivalTime + (orderSize * averageSelectionDuration) + waitDuration + checkoutDuration;
        }

        return returnTime;
    }

    public void setStatus(int status) {
        // Set's the customer's status:
        this.status = status;
    }

    public void setCheckoutDuration(double checkoutDuration) {
        // How long the customer spent scanning and paying for their items in checkout
        // (dependant on order size and lane type). Note, the lane they use determines
        // this timing:
        this.checkoutDuration = checkoutDuration;
    }

    public void addCheckoutDuration(double waitTime) {
        // Checkout lanes may have more than 1 customer in them. Each customer in line
        // before them adds to their waitTime:
        this.waitDuration += waitTime;
    }

    public void addToCheckoutLane(Checkout checkoutLane) {
        // Add's the customer to the given checkout lane:
        this.checkoutLane = checkoutLane;
        checkoutLane.offer(this);
    }

    @Override
    public int compareTo(Customer other) {
        // Look at the customer's current status to determine what their position in the
        // event queue should be in comparison to other customers.

        if (this.getEventTime() > other.getEventTime()) {
            return 1;
        } else if (this.getEventTime() < other.getEventTime()) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Customer [arrivalTime=" + arrivalTime + ", averageSelectionDuration=" + averageSelectionDuration
                + ", checkoutDuration=" + checkoutDuration + ", customerNumber=" + customerNumber + ", endShoppingTime="
                + endShoppingTime + ", finishTime=" + finishTime + ", orderSize=" + orderSize + ", status=" + status
                + ", waitDuration=" + waitDuration + "]";
    }
}
