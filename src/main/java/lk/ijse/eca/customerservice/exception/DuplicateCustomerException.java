package lk.ijse.eca.customerservice.exception;

public class DuplicateCustomerException extends RuntimeException {
    public DuplicateCustomerException(String nic) {
        super("Customer with NIC '" + nic + "' already exists");
    }
}
