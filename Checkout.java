// Adrien Abbey, CS-1181L-07, Mar. 24, 2022
// Checkout class for Project 3

// Checkout lanes are Queues (LinkedLists) for Customers.
// Checkout lanes come in two types: regular and express.

import java.util.LinkedList;

public abstract class Checkout extends LinkedList<Customer> {

    /* Methods */

    // Abstract method needed to calculate how long a customer takes checking out
    // their items.
    public abstract Double getCheckoutDuration(Customer customer);

    // Provide a method by which the lane can track how many customers it served:
    public abstract void addCustomerCount();

    // Return how many customers this lane has served:
    public abstract int getCustomerCount();

    // After a customer joins a lane queue, pass the current lane size to this
    // method. If the queue size is bigger than the previous, update the variable.
    public abstract void updateQueueSize();

    // Return the max queue size:
    public abstract int getMaxQueueSize();

    // Return the lane's name:
    public abstract String getName();
}
