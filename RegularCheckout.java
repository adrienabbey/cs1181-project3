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
    public String getName() {
        return name;
    }

    @Override
    public Double getCheckoutDuration(Customer customer) {
        // Calculate and return the given Customer's checkout duration:
        return customer.getOrderSize() * 0.05 + 2.0;
    }

    @Override
    public int compareTo(Checkout other) {
        // Compare the checkout lane queue sizes:
        // FIXME: Test this method!
        return other.size() - this.size();
    }
}
