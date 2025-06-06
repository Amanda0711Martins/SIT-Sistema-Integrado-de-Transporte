package com.sittransportadora.controller.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private String name;
    private String cnpj;
    private String cpf;
    private String email;
    private String phone;
    private String address;

    private LocalDateTime createDate;
    private LocalDateTime alterDate;
    private LocalDateTime bornDate;
}
