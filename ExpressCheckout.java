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
    public int compareTo(Checkout other) {
        // Compare the queue lengths of each checkout lane:
        return this.size() - other.size();
    }

    @Override
    public String toString() {
        return name + " (" + (size() - 1) + ")";
    }
}
