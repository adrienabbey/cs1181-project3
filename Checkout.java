// Adrien Abbey, CS-1181L-07, Mar. 24, 2022
// Checkout class for Project 3

// Checkout lanes are Queues (LinkedLists) for Customers.
// Checkout lanes come in two types: regular and express.

import java.util.LinkedList;

public abstract class Checkout extends LinkedList<Customer> implements Comparable<Checkout> {

    /* Methods */
    public abstract Double checkout(Customer customer);

}
