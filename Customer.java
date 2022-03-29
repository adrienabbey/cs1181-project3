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
    private double checkoutTime; // Time the customer finished filling their cart (in minutes relative to store
                                 // opening)
    private Double waitDuration; // Time the customer waited to start checking out, if any.
    private Double waitTime; // Time the customer is waiting for to check out if any.
    private double checkoutDuration; // How long it took the customer to scan their items and pay for them.
    private int status; // Tracks what the customer's status is. See getStatus() method for details.
    private Checkout checkoutLane; // Tracks what lane the customer is in, if any.

    /* Constructor */

    public Customer(double arrivalTime, int orderSize, double averageSelectionTime) {
        this.customerNumber = customerCount;
        customerCount++;
        this.arrivalTime = arrivalTime;
        this.itemCount = orderSize;
        this.averageSelectionDuration = averageSelectionTime;
        this.checkoutTime = arrivalTime + (this.itemCount * this.averageSelectionDuration);
        this.status = 0; // When first added to the event queue (loading of customer data), their status
                         // is 0, meaning they haven't entered the store yet.
        this.waitTime = null;
        this.checkoutLane = null; // Customers don't have a checkout lane at first.
        this.waitDuration = null; // Customers don't have a wait duration at first. Null will hopefully help me
                                  // error check issues.
    }

    /* Methods */

    public double getArrivalTime() {
        return arrivalTime;
    }

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

    public double getCheckoutTime() {
        return checkoutTime;
    }

    public Double getWaitTime() {
        return waitTime;
    }

    public String getName() {
        // Return a string of this customer's name:
        return "Customer " + customerNumber;
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
        return arrivalTime + (itemCount * averageSelectionDuration);
    }

    public double getEndCheckoutTime() {
        // Return the time the customer finished checking out (relative to store
        // opening):
        return arrivalTime + (itemCount * averageSelectionDuration) + waitDuration + checkoutDuration;
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
            returnTime = arrivalTime + (itemCount * averageSelectionDuration);
        }

        // If their status is 2, they've finished filling their cart and are waiting on
        // others in their checkout lane:
        if (status == 2) {
            returnTime = waitTime;
        }

        // If their status is 3, they're currently checking out:
        if (status == 3) {
            returnTime = arrivalTime + (itemCount * averageSelectionDuration) + waitDuration + checkoutDuration;
        }

        // If their status is 4, they've finished checking out and left the store:
        if (status == 4) {
            returnTime = arrivalTime + (itemCount * averageSelectionDuration) + waitDuration + checkoutDuration;
        }

        return returnTime;
    }

    public void setStatus(int status) {
        // Set's the customer's status:
        this.status = status;

        // Calculate any important statistics:
        if (status == 2) {
            waitTime = checkoutTime;
            // When a customer first gets set to ready to checkout, their waitDuration is
            // initially 0.0:
            waitDuration = 0.0;
        }
    }

    public void setCheckoutDuration(double checkoutDuration) {
        // How long the customer spent scanning and paying for their items in checkout
        // (dependant on order size and lane type). Note, the lane they use determines
        // this timing:
        this.checkoutDuration = checkoutDuration;
    }

    public void setWaitDuration(double waitDuration) {
        // Set the given customer's waitTime:
        this.waitDuration = waitDuration;
    }

    public void setCheckoutLane(Checkout checkout) {
        // Set the customer's checkout lane:
        this.checkoutLane = checkout;
    }

    public void setWaitTime(double waitTime) {
        // Set this customer's waitTime to the given time value:
        this.waitTime = waitTime;

        // Update this customer's waitDuration as well:
        waitDuration = waitTime - getEndShoppingTime();
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
            // These customers have the same wait time.
            // If the other customer is checking out and this one is waiting to check out:
            if ((this.getStatus() == 2) && (other.getStatus() == 3)) {
                // Then the other customer takes priority:
                return 1;
            } else if ((other.getStatus() == 2) && (this.getStatus() == 3)) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
