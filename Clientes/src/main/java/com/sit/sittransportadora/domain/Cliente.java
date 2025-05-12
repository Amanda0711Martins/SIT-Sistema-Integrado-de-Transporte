package com.sit.sittransportadora.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Cliente {
    @Id
    private Long id;

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
