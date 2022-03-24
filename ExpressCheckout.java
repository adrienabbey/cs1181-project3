// Adrien Abbey, CS-1181L-07, Mar. 24, 2022
// ExpressCheckout class for Project 3

// Express lanes are restricted to customers with 12 or fewer items.
// Express lanes have an average checkout time of 0.10 minutes per item, 
// plus 1.0 minutes to process payment and coupons.

public class ExpressCheckout extends Checkout {

    @Override
    public Double checkout(Customer customer) {
        // Calculate and return the given Customer's checkout duration:
        return customer.getOrderSize() * 0.10 + 1.0;
    }
}
