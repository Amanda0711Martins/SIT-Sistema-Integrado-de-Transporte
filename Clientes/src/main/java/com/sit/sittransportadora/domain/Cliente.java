package com.sit.sittransportadora.domain;

import com.sit.sittransportadora.controller.dto.LoginRequest;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
public class Cliente {
    @Id
    private UUID uuid;
    private String name;
    private String cnpj;
    private String cpf;
    private String email;
    private String phone;
    private String address;
    private String password;

    @ManyToMany()
    @JoinTable(
            name = "cliente_role",
            joinColumns = @JoinColumn(name = "cliente_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    private LocalDateTime createDate;
    private LocalDateTime alterDate;
    private LocalDateTime bornDate;

    public boolean isLoginCorrect(LoginRequest loginRequest, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(loginRequest.password(),this.password);
    }

}
