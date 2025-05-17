package com.logistics.financial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private Long id;
    private String name;
    private String documentNumber;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String postalCode;
}