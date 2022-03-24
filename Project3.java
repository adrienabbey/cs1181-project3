/*
Adrien Abbey, CS-1181L-07, Mar. 19, 2022
Project 3: Data Science (Grocery Store Simulation)
Visual Studio Code, Windows 10, Eclipse Temurin JDK/JRE 11

The Problem: There are twelve checkout lanes.  Some of these lanes can be 
express lanes, limited to 12 items.  The goal is to minimize the overall time 
that customers wait in line.  How many of these 12 lanes should be express 
lanes?

Data is collected from a 'normal/average' set of customers during a 
normal/average day.  This data includes:
  - Time of arrival at store (in minutes relative from the store opening)
  - Their order size (number of items purchased)
  - Average time it takes to select each grocery item (total time spent 
    browsing divided by order size)

This information is provided in a text file.  Each customer is one line in the 
data file, characterized by these three measurements in order.

For example, "2.0 15 0.5" represents a customer that arrives at the store 2.0 
minutes after opening.  The customer picks up 15 items and on average spends 
0.5 minutes to pick each item.  The customer therefore takes 7.5 minutes 
shopping and is ready to check out 9.5 minutes after the store opens.

The customer then moves to the checkout area.  Assume that customers will 
always select the checkout line with the shortest number of customers in 
line.  Customers with more than 12 items can only use regular checkout lines, 
while customers with 12 or fewer items will use any lane, preferring express 
lanes if they have a line length equal to a regular lane.

If there is no one in line, then the customer can immediately start checking 
out.  If there's anyone in line, the customer must wait for everyone in front 
of them to check out before they begin.

Time to check out is determined by the lane type and size of their order.  
Regular lanes have an average checkout time of 0.05 minutes per item plus 2.0 
minutes to process payment.  Express lanes have an average checkout time of 0.
10 minutes per item plus 1.0 minutes to process payment.

Continuing the example, our customer with 15 items moves to check out 9.5 
minutes after opening.  Since they have 15 items, they must use a regular 
checkout lane.  Let's assume there is a regular lane with zero customers.
They immediately begin to check out: 15 items * 0.05 minutes per item + 2.0 
minutes to process payment = 2.75 minutes.  This customer leaves the store
12.25 minutes after the store opens (9.5 min arrival at checkout + 0 wait + 
2.75 min checkout).  If all the lanes had one customer, this customer would 
need to wait for them to finish before starting.

Use a discrete event simulation to simulate the grocery store.  This means 
time is managed by using a simulation of a clock that advances whenever an 
event occurs.  For example, if the simulation time is 0.0 minutes and a 
customer enters the store at 2.0 minutes, we mark the passage of time by 
advancing the simulation clock to 2.0 minutes.  If a customer reaches a 
checkout line 7.5 minutes later, we advance the clock to 9.5.  Our assumption 
is no interesting activities occur between events.

    A customer arrived -- schedule the next arrival
    Customer starts shopping -- schedule the customer to end shopping
    Customer ends shopping -- put the customer in a checkout line
        If customer is first in line, schedule this customer to end checkout
    Customer ends checkout -- if another customer is waiting in line,
        schedule that customer to end checkout

NOTES:
  - I'm going to do something a little different than what was suggested in 
    the instructions: I'm going to have a single event queue that tracks all 
    events (a PriorityQueue).
  - As customers reach the top of the event queue, I pull them out and adjust 
    their 'status', which in turn adjusts their compareTo value.  If they 
    still have more to do, I put them back into the queue which sorts them 
    using their new compareTo value.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

class Project3 {
    /* Variables */
    private static String dataFileName = "./dataFiles/arrival.txt";
    private static int checkoutLanes = 12; // total number of checkout lanes
    private static int expressLanes = 4; // number of express lanes
    private static int regularLanes = checkoutLanes - expressLanes; // number of regular lanes

    public static void main(String[] args) {
        // Load the customer data:
        ArrayList<Customer> customerList = loadCustomers(dataFileName);
        // TODO: Is it okay for this to be an ArrayList?

        // Create a PriorityQueue for customer checkout times:
        PriorityQueue<Customer> eventQueue = new PriorityQueue<>();

        // Add customers to the event queue:
        // NOTE: This compares each customer's ready-to-checkout time relative to store
        // opening. The time they arrived and the time they took to fill their cart are
        // already accounted for and calculated.
        for (Customer c : customerList) {
            eventQueue.offer(c);
        }

        // FIXME TEST: Print out the customer list:
        for (int i = 0; i < eventQueue.size(); i++) {
            System.out.println(eventQueue.poll());
        }

        // Create the checkout lanes:
        // FIXME: How do I look through this ArrayList to find the most appropriate lane
        // for a given customer?
        ArrayList<Checkout> checkoutLanes = createCheckoutLanes();

        // TODO: Do I need this?
        Double eventTime = 0.0; // Track the current time (relative to store opening)

        // Start looping through the event queue:
        while (true) {
            // Poll the first customer from the event queue:
            Customer customer = eventQueue.poll();

            // Adjust the current event time:
            eventTime = customer.getEventTime();

            // If that customer just arrived:
            if (customer.getStatus() == 0) {
                // then set that customer's status to 1 and re-add them to the queue:
                customer.setStatus(1); // Customer is now selecting items and filling their cart
                eventQueue.offer(customer);
            } else if (customer.getStatus() == 1) {
                // If the customer was filling their cart:
                // then that customer has finished and is ready to checkout:
                customer.setStatus(2);
                // TODO: Add this customer to a checkout lane.
                // TODO: Determine if this customer is waiting to checkout.
                eventQueue.offer(customer);
            }

            // FIXME: Don't loop more than once for now. Loop unfinished.
            break;
        }

    }

    private static ArrayList<Customer> loadCustomers(String fileName) {
        // Load the customer list from the given file name.

        // Create an ArrayList to hold the new Customer objects:
        ArrayList<Customer> customerList = new ArrayList<>();

        // Try-catch block to handle exceptions:
        try {
            // Create a scanner object to grab data from the given file name:
            Scanner fileScanner = new Scanner(new File(fileName));

            // I'm going to assume the given data file is properly formatted.

            // Keep trying to load data from the file:
            while (true) {
                // Assume that if there's a double to load, there's a complete data set for a
                // customer:
                if (fileScanner.hasNextDouble()) {
                    // Grab customer information and assign to variables:
                    Double arrivalTime = fileScanner.nextDouble();
                    int orderSize = fileScanner.nextInt();
                    Double averageSelectionTime = fileScanner.nextDouble();

                    // Create a new customer using the given data:
                    customerList.add(new Customer(arrivalTime, orderSize, averageSelectionTime));
                } else {
                    // If there's no more data, break the loop:
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("The customer data file was not found.  Aborting.");
            System.exit(1); // Exit the program, file not found.
        }

        return customerList;
    }

    private static ArrayList<Checkout> createCheckoutLanes() {
        // Creates and returns an ArrayList of the store's checkout lanes:

        // Create an ArrayList to hold the checkout lanes:
        ArrayList<Checkout> checkoutList = new ArrayList<>();

        // Create the regular checkout lanes:
        for (int i = 0; i < checkoutLanes - expressLanes; i++) {
            // TODO: Do lanes need names?
            checkoutList.add(new RegularCheckout());
        }

        // Create the express checkout lanes:
        for (int i = 0; i < expressLanes; i++) {
            checkoutList.add(new ExpressCheckout());
        }

        return checkoutList;
    }
}