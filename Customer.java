// Adrien Abbey, CS-1181L-07, Mar. 19, 2022
// Customer class for Project 3

public class Customer implements Comparable<Customer> {

    /* Fields */

    private static int customerCount = 0; // Track how many customers there are. Also used to determine customer number.
    private int customerNumber; // Assign customers an ID number
    private double arrivalTime; // Time of arrival at the store (in minutes relative from the store opening)
    private int itemCount; // Order size (the number of items they purchase)
    private double averageSelectionDuration; // The average time it takes to select each item (total time spent browsing
                                             // divided by order size)
    private double endShoppingTime; // Time the customer finished filling their cart (in minutes relative to store
    // opening)
    private Double startCheckoutTime; // Time the customer starts checking out after waiting for others to finish, if
                                      // any.
    private Double waitDuration; // Time the customer waited to start checking out, if any.
    private Double checkoutDuration; // How long it took the customer to scan their items and pay for them.
    private int status; // Tracks what the customer's status is. See getStatus() method for details.
    private Checkout checkoutLane; // Tracks what lane the customer is in, if any.
    private int checkoutLaneLength; // Track how long the customer's checkout lane was before they joined it.

    /* Constructor */

    public Customer(double arrivalTime, int orderSize, double averageSelectionTime) {
        this.customerNumber = customerCount;
        customerCount++;
        this.arrivalTime = arrivalTime;
        this.itemCount = orderSize;
        this.averageSelectionDuration = averageSelectionTime;
        this.status = 0; // When first added to the event queue (loading of customer data), their status
                         // is 0, meaning they haven't entered the store yet.
        this.endShoppingTime = arrivalTime + (this.itemCount * this.averageSelectionDuration);

        // The following are null values for error checking purposes:
        this.startCheckoutTime = null; // When the customer starts checking out, which may include waiting for others
                                       // to finish first.
        this.checkoutDuration = null; // How long it takes to checkout, determined by checkout lane type.
        this.checkoutLane = null; // Track which checkout lane the customer is in (or was in).
        this.waitDuration = null; // Track how long the customer had to wait for others to finish.

    }

    /* Methods */

    public int getItemCount() {
        return itemCount;
    }

    public Checkout getCheckoutLane() {
        return checkoutLane;
    }

    public double getWaitDuration() {
        return waitDuration;
    }

    public double getCheckoutDuration() {
        return checkoutDuration;
    }

    public double getStartCheckoutTime() {
        return startCheckoutTime;
    }

    public double getEndShoppingTime() {
        return endShoppingTime;
    }

    public String getName() {
        // Return a string of this customer's name:
        return "Customer " + customerNumber;
    }

    public int getStatus() {
        // Returns the customer's current status:
        // 0: customer hasn't entered the store yet
        // 1: customer is currently selecting items and filling their cart
        // 2: customer is in a checkout lane
        // 3: customer has finished and left the store
        // null: something went horribly wrong

        return status;
    }

    public double getEventTime() {
        // Looks at the customer's current status and calculates when the time of their
        // NEXT event.

        Double returnTime = null;

        // When customers are first loaded, their status is 0: they have not arrived
        // yet. Return their arrival time:
        if (status == 0) {
            returnTime = arrivalTime;
        }

        // If their status is 1, they've started shopping: return the time when they'll
        // finish filling their cart:
        if (status == 1) {
            returnTime = arrivalTime + (itemCount * averageSelectionDuration);
        }

        // If their status is 2, they're currently checking out:
        if (status == 2) {
            returnTime = arrivalTime + (itemCount * averageSelectionDuration) + waitDuration + checkoutDuration;
        }

        // If their status is 3, they've finished checking out and left the store:
        if (status == 3) {
            returnTime = arrivalTime + (itemCount * averageSelectionDuration) + waitDuration + checkoutDuration;
        }

        return returnTime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStartCheckoutTime(double startCheckoutTime) {
        // Sets the time when the customer can start checking out
        this.startCheckoutTime = startCheckoutTime;
        // With this time, their wait duration can also be calculated
        this.waitDuration = this.startCheckoutTime - this.endShoppingTime;
    }

    public void setCheckoutDuration(double checkoutDuration) {
        // Sets how long the customer spent scanning and paying for their items in
        // checkout (dependant on order size and lane type). Note, the lane they use
        // determines this timing:
        this.checkoutDuration = checkoutDuration;
    }

    public void setCheckoutLane(Checkout checkout) {
        // Set the customer's checkout lane (for tracking purposes, not the same as
        // adding the customer to the lane):
        this.checkoutLaneLength = checkout.size();
        this.checkoutLane = checkout;
    }

    public int getCheckoutLaneLength() {
        // Returns the length of the customer's checkout lane when they joined it:
        return checkoutLaneLength;
    }

    @Override
    public int compareTo(Customer other) {
        // Look at the customer's current status to determine what their position in the
        // event queue should be in comparison to other customers.

        // Problem: There can be multiple customers waiting to check out at the head of
        // the eventQueue. I need to sort based on their position in their respective
        // lanes. If they have the same eventTime and the same status, check their lane
        // position.

        if (this.getEventTime() > other.getEventTime()) {
            return 1;
        } else if (this.getEventTime() < other.getEventTime()) {
            return -1;
        } else {
            // These customers have the same wait time.
            // Compare the customer's status, giving priority to the higher number:
            if (this.getStatus() < other.getStatus()) {
                return 1;
            } else if (this.getStatus() > other.getStatus()) {
                return -1;
            } else {
                // A customer at the front of their lane should go first:
                if (this.getStatus() == 2 && other.getStatus() == 2) {
                    if (this.getCheckoutLane().peek() == this) {
                        return -1;
                    }
                    if (other.getCheckoutLane().peek() == other) {
                        return 1;
                    }
                }
                return 0;
            }
        }
    }
}
