package lk.ijse.eca.customerservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lk.ijse.eca.customerservice.dto.CustomerRequestDTO;
import lk.ijse.eca.customerservice.dto.CustomerResponseDTO;
import lk.ijse.eca.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CustomerController {

    private final CustomerService customerService;
        private static final String NIC_REGEXP = "^(\\d{9}[vV]|\\d{12})$";

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerResponseDTO> createCustomer(
            @Validated({Default.class, CustomerRequestDTO.OnCreate.class}) @ModelAttribute CustomerRequestDTO dto) {
        log.info("POST /api/v1/customers - NIC: {}", dto.getNic());
        CustomerResponseDTO response = customerService.createCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{nic}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable @Pattern(regexp = NIC_REGEXP,
                    message = "NIC must be 9 digits followed by V or v OR 12 digits") String nic,
            @Valid @ModelAttribute CustomerRequestDTO dto) {
        log.info("PUT /api/v1/customers/{}", nic);
        CustomerResponseDTO response = customerService.updateCustomer(nic, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{nic}")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable @Pattern(regexp = NIC_REGEXP,
                    message = "NIC must be 9 digits followed by V or v OR 12 digits") String nic) {
        log.info("DELETE /api/v1/customers/{}", nic);
        customerService.deleteCustomer(nic);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{nic}")
    public ResponseEntity<CustomerResponseDTO> getCustomer(
            @PathVariable @Pattern(regexp = NIC_REGEXP,
                    message = "NIC must be 9 digits followed by V or v OR 12 digits") String nic) {
        log.info("GET /api/v1/customers/{}", nic);
        CustomerResponseDTO response = customerService.getCustomer(nic);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        log.info("GET /api/v1/customers");
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{nic}/picture")
    public ResponseEntity<byte[]> getCustomerPicture(
            @PathVariable @Pattern(regexp = NIC_REGEXP,
                    message = "NIC must be 9 digits followed by V or v OR 12 digits") String nic) {
        log.info("GET /api/v1/customers/{}/picture", nic);
        byte[] picture = customerService.getCustomerPicture(nic);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(picture);
    }
}
