// Adrien Abbey, CS-1181L-07, Mar. 24, 2022
// ExpressCheckout class for Project 3

// Express lanes are restricted to customers with 12 or fewer items.
// Express lanes have an average checkout time of 0.10 minutes per item, 
// plus 1.0 minutes to process payment and coupons.

public class ExpressCheckout extends Checkout {

    /* Fields */
    private String name;

    /* Constructor */
    public ExpressCheckout(String name) {
        super();
        this.name = name;
    }

    /* Methods */

    @Override
    public Double getCheckoutDuration(Customer customer) {
        // Calculate and return the given Customer's checkout duration:
        return customer.getItemCount() * 0.10 + 1.0;
    }

    @Override
    public String toString() {
        // Used for event logging, return the lane name and how many people are in the
        // lane, minus one (to replicate example log data, likely intended to show how
        // many OTHER people are in the lane)
        return name + " (" + (size() - 1) + ")";
    }
}
