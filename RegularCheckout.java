// Adrien Abbey, CS-1181L-07, Mar. 24, 2022
// RegularCheckout class for Project 3

// Regular lanes have no restrictions on order size.
// Regular lanes have an average checkout time of 0.05 minutes per item in the order,
// plus 2.0 minutes to process payment and coupons.

public class RegularCheckout extends Checkout {

    /* Fields */
    private String name;

    /* Constructor */
    public RegularCheckout(String name) {
        super();
        this.name = name;
    }

    /* Methods */

    @Override
    public Double getCheckoutDuration(Customer customer) {
        // Calculate and return the given Customer's checkout duration:
        return customer.getItemCount() * 0.05 + 2.0;
    }

    @Override
    public String toString() {
        // Used for event logging, return the lane name and how many people are in the
        // lane, minus one (to replicate example log data, likely intended to show how
        // many OTHER people are in the lane)
        return name + " (" + (size() - 1) + ")";
    }
}
