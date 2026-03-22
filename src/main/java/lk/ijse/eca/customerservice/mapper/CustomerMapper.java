package lk.ijse.eca.customerservice.mapper;

import lk.ijse.eca.customerservice.dto.CustomerRequestDTO;
import lk.ijse.eca.customerservice.dto.CustomerResponseDTO;
import lk.ijse.eca.customerservice.entity.Customer;
import org.mapstruct.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class CustomerMapper {
    @Mapping(target = "picture", expression = "java(buildPictureUrl(customer))")
    public abstract CustomerResponseDTO toResponseDto(Customer customer);

    @Mapping(target = "picture", ignore = true)
    public abstract Customer toEntity(CustomerRequestDTO dto);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "nic", ignore = true)
    @Mapping(target = "picture", ignore = true)
    public abstract void updateEntity(CustomerRequestDTO dto, @MappingTarget Customer customer);

    protected String buildPictureUrl(Customer customer) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/customers/{nic}/picture")
                .buildAndExpand(customer.getNic())
                .toUriString();
    }

}
