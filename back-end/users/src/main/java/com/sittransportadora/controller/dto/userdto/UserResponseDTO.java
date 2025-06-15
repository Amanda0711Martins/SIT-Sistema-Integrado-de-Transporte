package com.sittransportadora.controller.dto.userdto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
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
