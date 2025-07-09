package com.logistica.customer.dto;

import com.logistica.customer.model.Customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String name;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$", message = "CNPJ deve estar no formato XX.XXX.XXX/XXXX-XX")
    private String cnpj;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}$", message = "Telefone deve estar no formato (XX) XXXX-XXXX ou (XX) XXXXX-XXXX")
    private String phone;

    @NotBlank(message = "Endereço é obrigatório")
    private String address;

    @NotBlank(message = "Cidade é obrigatória")
    private String city;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    private String state;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "CEP deve estar no formato XXXXX-XXX")
    private String zipCode;

    @Size(max = 500)
    private String notes;

    private Customer.CustomerStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Factory method para converter de Customer para CustomerDTO
    public static CustomerDTO fromEntity(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setCnpj(customer.getCnpj());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setCity(customer.getCity());
        dto.setState(customer.getState());
        dto.setZipCode(customer.getZipCode());
        dto.setNotes(customer.getNotes());
        dto.setStatus(customer.getStatus());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());
        return dto;
    }

    // Factory method para converter de CustomerDTO para Customer
    public Customer toEntity() {
        Customer customer = new Customer();
        customer.setId(this.id);
        customer.setName(this.name);
        customer.setCnpj(this.cnpj);
        customer.setEmail(this.email);
        customer.setPhone(this.phone);
        customer.setAddress(this.address);
        customer.setCity(this.city);
        customer.setState(this.state);
        customer.setZipCode(this.zipCode);
        customer.setNotes(this.notes);
        customer.setStatus(this.status != null ? this.status : Customer.CustomerStatus.ACTIVE);
        return customer;
    }
}