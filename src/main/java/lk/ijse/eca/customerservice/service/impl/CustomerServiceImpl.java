package lk.ijse.eca.customerservice.service.impl;

import lk.ijse.eca.customerservice.dto.CustomerRequestDTO;
import lk.ijse.eca.customerservice.dto.CustomerResponseDTO;
import lk.ijse.eca.customerservice.entity.Customer;
import lk.ijse.eca.customerservice.exception.CustomerNotFoundException;
import lk.ijse.eca.customerservice.exception.DuplicateCustomerException;
import lk.ijse.eca.customerservice.exception.FileOperationException;
import lk.ijse.eca.customerservice.mapper.CustomerMapper;
import lk.ijse.eca.customerservice.repository.CustomerRepository;
import lk.ijse.eca.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Value("${app.storage.path}")
    private String storagePathStr;

    private Path storagePath;

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {
        log.debug("Creating customer with NIC: {}", dto.getNic());

        if (customerRepository.existsById(dto.getNic())) {
            log.warn("Duplicate NIC detected: {}", dto.getNic());
            throw new DuplicateCustomerException(dto.getNic());
        }

        String pictureId = UUID.randomUUID().toString();

        Customer customer = customerMapper.toEntity(dto);
        customer.setPicture(pictureId);

        customerRepository.save(customer);
        log.debug("Customer persisted to DB: {}", dto.getNic());

        savePicture(pictureId, dto.getPicture());

        log.info("Customer created successfully: {}", dto.getNic());
        return customerMapper.toResponseDto(customer);
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(String nic, CustomerRequestDTO dto) {
        log.debug("Updating customer with NIC: {}", nic);

        Customer customer = customerRepository.findById(nic)
                .orElseThrow(() -> {
                    log.warn("Customer not found for update: {}", nic);
                    return new CustomerNotFoundException(nic);
                });

        String oldPictureId = customer.getPicture();
        boolean pictureChanged = dto.getPicture() != null && !dto.getPicture().isEmpty();
        String newPictureId = pictureChanged ? UUID.randomUUID().toString() : oldPictureId;

        customerMapper.updateEntity(dto, customer);
        customer.setPicture(newPictureId);

        customerRepository.save(customer);
        log.debug("Customer updated in DB: {}", nic);

        if (pictureChanged) {
            savePicture(newPictureId, dto.getPicture());
            tryDeletePicture(oldPictureId);
        }

        log.info("Customer updated successfully: {}", nic);
        return customerMapper.toResponseDto(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(String nic) {
        log.debug("Deleting customer with NIC: {}", nic);

        Customer customer = customerRepository.findById(nic)
                .orElseThrow(() -> {
                    log.warn("Customer not found for deletion: {}", nic);
                    return new CustomerNotFoundException(nic);
                });

        String pictureId = customer.getPicture();

        customerRepository.delete(customer);
        log.debug("Customer marked for deletion in DB: {}", nic);

        deletePicture(pictureId);

        log.info("Customer deleted successfully: {}", nic);
    }

    @Override
    @Transactional
    public CustomerResponseDTO getCustomer(String nic) {
        log.debug("Fetching customer with NIC: {}", nic);
        return customerRepository.findById(nic)
                .map(customerMapper::toResponseDto)
                .orElseThrow(() -> {
                    log.warn("Customer not found: {}", nic);
                    return new CustomerNotFoundException(nic);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAllCustomers() {
        log.debug("Fetching all customers");
        List<CustomerResponseDTO> customers = customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponseDto)
                .collect(Collectors.toList());
        log.debug("Fetched {} customers", customers.size());
        return customers;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getCustomerPicture(String nic) {
        log.debug("Fetching picture for customer NIC: {}", nic);
        Customer customer = customerRepository.findById(nic)
                .orElseThrow(() -> {
                    log.warn("Customer not found: {}", nic);
                    return new CustomerNotFoundException(nic);
                });
        Path filePath = storagePath().resolve(customer.getPicture());
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to read picture for customer: {}", nic, e);
            throw new FileOperationException("Failed to read picture for customer: " + nic, e);
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////


    private Path storagePath() {
        if (storagePath == null) {
            storagePath = Paths.get(storagePathStr);
        }
        try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new FileOperationException(
                    "Failed to create storage directory: " + storagePath.toAbsolutePath(), e);
        }
        return storagePath;
    }

    private void savePicture(String pictureId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileOperationException("Picture file must not be empty");
        }
        Path filePath = storagePath().resolve(pictureId);
        try {
            Files.write(filePath, file.getBytes());
            log.debug("Picture saved: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to save picture: {}", filePath, e);
            throw new FileOperationException("Failed to save picture file: " + pictureId, e);
        }
    }

    private void deletePicture(String pictureId) {
        Path filePath = storagePath().resolve(pictureId);
        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.debug("Picture deleted: {}", filePath);
            } else {
                log.warn("Picture file not found on disk (already removed?): {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete picture: {}", filePath, e);
            throw new FileOperationException("Failed to delete picture file: " + pictureId, e);
        }
    }

    private void tryDeletePicture(String pictureId) {
        try {
            deletePicture(pictureId);
        } catch (FileOperationException e) {
            log.warn("Could not delete old picture file '{}'. Manual cleanup may be required.", pictureId);
        }
    }
}
