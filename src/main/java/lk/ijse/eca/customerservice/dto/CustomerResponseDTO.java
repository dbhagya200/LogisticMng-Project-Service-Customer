package lk.ijse.eca.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CustomerResponseDTO {

    private String nic;
    private String name;
    private String address;
    private String postalCode;
    private String mobile;
    private String email;
    private String picture;
}
