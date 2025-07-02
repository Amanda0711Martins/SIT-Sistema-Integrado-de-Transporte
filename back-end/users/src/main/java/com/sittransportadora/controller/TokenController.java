package com.sittransportadora.controller;

import com.sittransportadora.controller.dto.LoginRequest;
import com.sittransportadora.controller.dto.LoginResponse;
<<<<<<< HEAD
import com.sittransportadora.model.Role;
import com.sittransportadora.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
=======
import com.sittransportadora.controller.dto.UserDTO;
import com.sittransportadora.controller.dto.userdto.AuthStatus;
import com.sittransportadora.controller.dto.userdto.AuthStatusResponse;
import com.sittransportadora.controller.dto.userdto.UserResponseRoleDTO;
import com.sittransportadora.model.User;
import com.sittransportadora.model.Role;
import com.sittransportadora.service.ClienteService;
import com.sittransportadora.service.RoleService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
>>>>>>> 3abf589d6c4871ce5a750469f7e7a8017ca7d79d
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
public class TokenController {

    private final JwtEncoder jwtEncoder;
    private ClienteRepository clienteRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public TokenController(JwtEncoder jwtEncoder, ClienteRepository clienteRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.clienteRepository = clienteRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        var cliente = clienteRepository.findByEmail(loginRequest.email());
        if (cliente.isEmpty() || cliente.get().isLoginCorrect(loginRequest, bCryptPasswordEncoder)) {
            throw new BadCredentialsException("User or password is invalid");
        }

        var now = Instant.now();
        var expirensIn = 300L;

        var roles = cliente.get().getRoles().stream()
                .map(Role::getName)
                .toList();

        var claims = JwtClaimsSet.builder()
                .issuer("Backend")
                .subject(cliente.get().getUuid().toString())
                .claim("roles", roles)
                .expiresAt(now.plusSeconds(expirensIn))
                .issuedAt(now)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponse(jwtValue, expirensIn));

    }
}
