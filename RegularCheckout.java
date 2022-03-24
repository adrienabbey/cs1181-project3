// Adrien Abbey, CS-1181L-07, Mar. 24, 2022
// RegularCheckout class for Project 3

// Regular lanes have no restrictions on order size.
// Regular lanes have an average checkout time of 0.05 minutes per item in the order,
// plus 2.0 minutes to process payment and coupons.

public class RegularCheckout extends Checkout {

    @Override
    public Double checkout(Customer customer) {
        // Calculate and return the given Customer's checkout duration:
        return customer.getOrderSize() * 0.05 + 2.0;
    }
}
