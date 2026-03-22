package lk.ijse.eca.customerservice.service;

import lk.ijse.eca.customerservice.dto.CustomerRequestDTO;
import lk.ijse.eca.customerservice.dto.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {

    CustomerResponseDTO createCustomer(CustomerRequestDTO dto);

    CustomerResponseDTO updateCustomer(String nic, CustomerRequestDTO dto);

    void deleteCustomer(String nic);

    CustomerResponseDTO getCustomer(String nic);

    List<CustomerResponseDTO> getAllCustomers();

    byte[] getCustomerPicture(String nic);
}
