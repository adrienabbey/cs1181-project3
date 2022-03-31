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
  - Because of how I handle the eventQueue timing of lanes with more than one 
    customer (I look at the time when the front of the queue will finish 
    checking out and set each customer to act after that), there will be a very
    small change in queue times, which hopefully will be accepted as a rounding
    error.  I know it's not ideal, and there's likely a better solution than
    what I picked, but I don't want to go back and refactor all my code to fix
    this.
  - The instruction PDF states that "customers will always select the checkout 
    area with the shortest number of other customers in line".  The in-class 
    description suggested otherwise.  I'm going to assume it's safe to use the 
    method described in the PDF instead.
  - I spent DAYS trying to figure out why my code was buggy.  Turns out I can't
    easily resort a PriorityQueue when the sorting of specific objects change
    in the middle of that list.  I thought PriorityQueueObj.remove(specificObj)
    would remove a specific object, but all it did was remove the head.  Then
    when I went to readd that object, it just created duplicates of an existing
    object.

TODO:
  - Resolve code duplication.
  - Statistic gathering.
  - Check grading outline.
  - Write document.
  - Remove unused methods.
  - Code review.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

class Project3 {
    /* Variables */
    private static String dataFileName = "./dataFiles/arrival.txt";
    private static int checkoutLaneCount = 12; // total number of checkout lanes
    private static int expressLaneCount = 3; // number of express lanes

    public static void main(String[] args) {
        // Load the customer data:
        ArrayList<Customer> customerList = loadCustomers(dataFileName);

        // Create a PriorityQueue for customer event times:
        PriorityQueue<Customer> eventQueue = new PriorityQueue<>();

        // Add customers to the event queue:
        // NOTE: This uses the customer's status number to calculate the time of their
        // next event, and sorts customers based on that.
        for (Customer c : customerList) {
            eventQueue.offer(c);
        }

        // Create the checkout lanes:
        LinkedList<Checkout> expressLanes = createCheckoutLanes(true); // true means create express lanes
        LinkedList<Checkout> regularLanes = createCheckoutLanes(false); // false means create regular lanes

        double currentTime = 0.0; // Track the current time (relative to store opening). This is essential for
                                  // tracking event times.

        // Start looping through the event queue:
        while (true) {
            // If there's no more customers, break the loop:
            if (eventQueue.size() == 0) {
                break;
            }

            // Poll the first customer from the event queue:
            Customer customer = eventQueue.poll();

            // Adjust the current event time:
            currentTime = customer.getEventTime();

            // Look at the customer's status and handle with care:
            if (customer.getStatus() == 0) {
                // If that customer just arrived:

                // Print to event log:
                System.out.println(String.format("%.2f: Arrival %s", currentTime, customer.getName()));

                // Then set that customer's status to 1 and re-add them to the queue:
                customer.setStatus(1); // Customer is now selecting items and filling their cart

            } else if (customer.getStatus() == 1) {
                // If the customer was filling their cart:

                // Print to event log:
                System.out.println(String.format("%.2f: Finished Shopping %s", currentTime, customer.getName()));

                // Then that customer has finished and is ready to checkout:
                customer.setStatus(2);

                // Select an appropriate checkout lane:

                // Track any customer who might be ahead of the given customer:
                Customer customerAheadInLine = null;

                // If the customer has 12 or fewer items:
                if (customer.getItemCount() <= 12) {
                    // Then this customer can use an express lane.

                    // Look for the shortest lane appropriate for this customer:
                    Checkout shortestLane = regularLanes.peek();
                    // FIXME: code duplication!
                    for (Checkout c : regularLanes) {
                        if (c.size() < shortestLane.size()) {
                            shortestLane = c;
                        }
                    }
                    // FIXME: This might constantly change equal length lanes, fix?
                    for (Checkout c : expressLanes) {
                        if (c.size() <= shortestLane.size()) {
                            shortestLane = c;
                        }
                    }

                    // Add this customer to that lane:
                    // FIXME: Code duplication!
                    if (shortestLane.size() > 0) {
                        customerAheadInLine = shortestLane.getLast();
                    }
                    shortestLane.addLast(customer);
                    customer.setCheckoutLane(shortestLane);
                    customer.setCheckoutDuration(customer.getCheckoutLane().getCheckoutDuration(customer));
                } else {
                    // Otherwise the customer must use a regular lane:
                    Checkout shortestLane = regularLanes.peek();
                    for (Checkout c : regularLanes) {
                        if (c.size() < shortestLane.size()) {
                            shortestLane = c;
                        }
                    }
                    if (shortestLane.size() > 0) {
                        customerAheadInLine = shortestLane.getLast();
                    }
                    shortestLane.addLast(customer);
                    customer.setCheckoutLane(shortestLane);
                    customer.setCheckoutDuration(customer.getCheckoutLane().getCheckoutDuration(customer));
                }

                // After adding the customer to a lane, check to see if the lane has another
                // customer in it:
                if (customer.getCheckoutLane().size() > 1) {
                    // If the checkout lane has someone else in it:
                    // Set this customer's wait time:
                    customer.setStartCheckoutTime(customerAheadInLine.getEventTime());
                } else {
                    // If the checkout lane is empty, this customer starts checking out:
                    customer.setStartCheckoutTime(currentTime);
                }
                // Print to event log:
                if (customer.getItemCount() <= 12) {
                    System.out.println("  12 or fewer, chose " + customer.getCheckoutLane());
                } else {
                    System.out.println("  More than 12, chose " + customer.getCheckoutLane());
                }
            } else if (customer.getStatus() == 2) {
                // If this customer was checking out:

                // Print to event log:
                System.out.println(String.format(
                        "%.2f: Finished Checkout %s on %s (%.2f minute wait, %d people in line -- finished shopping at %.2f, got to the front of the line at %.2f)",
                        currentTime, customer.getName(), customer.getCheckoutLane(), customer.getWaitDuration(),
                        (customer.getCheckoutLane().size() - 1), customer.getEndShoppingTime(),
                        (customer.getStartCheckoutTime())));

                // Then this customer finishes checking out:

                // The customer was checking out, and thus at the head of their lane's queue.
                // They're done, remove them from their checkout lane:
                customer.getCheckoutLane().remove();

                // This customer is ready to go home:
                customer.setStatus(3);
            }

            // If this customer has more to do:
            if (customer.getStatus() < 3) {
                // Then add this customer back into the event queue:
                eventQueue.offer(customer);
            }
        }

        // Print out statistics for this simulation:
        double avgWaitDuration = 0.0;
        double avgCheckoutDuration = 0.0;
        double avgOrderSize = 0.0;
        int customerCount = customerList.size();
        for (Customer c : customerList) {
            avgWaitDuration += c.getWaitDuration();
            avgCheckoutDuration += c.getCheckoutDuration();
            avgOrderSize += c.getItemCount();
        }
        System.out.println(String.format("Average order size: %.3f", (avgOrderSize / customerCount)));
        System.out.println(String.format("Average checkout time: %.3f", (avgCheckoutDuration / customerCount)));
        System.out.println(String.format("Average wait time: %.3f", (avgWaitDuration / customerCount)));
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
                    double arrivalTime = fileScanner.nextDouble();
                    int orderSize = fileScanner.nextInt();
                    double averageSelectionTime = fileScanner.nextDouble();

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

    private static LinkedList<Checkout> createCheckoutLanes(boolean express) {
        // Creates and returns a PriorityQueue of the store's checkout lanes.
        // If true, create express lanes. If false, create regular lanes.

        // Create a PriorityQueue to hold the checkout lanes:
        LinkedList<Checkout> q = new LinkedList<>();

        // If not express lanes:
        if (!express) {
            // Create the regular checkout lanes:
            for (int i = 0; i < (checkoutLaneCount - expressLaneCount); i++) {
                q.offer(new RegularCheckout("Regular Checkout " + (i + 1)));
            }
        }

        // If express lanes:
        if (express) {
            // Create the express checkout lanes:
            for (int i = 0; i < expressLaneCount; i++) {
                q.offer(new ExpressCheckout("Express Checkout " + (i + 1)));
            }
        }

        return q;
    }

}