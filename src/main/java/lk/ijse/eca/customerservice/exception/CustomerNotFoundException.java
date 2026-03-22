package lk.ijse.eca.customerservice.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String nic) {
        super("Customer with NIC '" + nic + "' not found");
    }
}
