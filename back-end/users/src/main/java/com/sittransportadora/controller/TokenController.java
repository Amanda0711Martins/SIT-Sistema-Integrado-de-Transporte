package com.sittransportadora.controller;

import com.sittransportadora.controller.dto.LoginRequest;
import com.sittransportadora.controller.dto.LoginResponse;
import com.sittransportadora.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;


@RestController
public class TokenController {

    private final JwtEncoder jwtEncoder;
    private ClienteRepository clienteRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public TokenController(JwtEncoder jwtEncoder, ClienteRepository clienteRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.clienteRepository = clienteRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        var cliente = clienteRepository.findByEmail(loginRequest.email());
        if(cliente.isEmpty() || !cliente.get().isLoginCorrect(loginRequest,bCryptPasswordEncoder)){
            throw new BadCredentialsException("User or password is invalid");
        }

        var now = Instant.now();
        //5 minutos
        var expirensIn = 300L;

        var claims = JwtClaimsSet.builder()
                .issuer("Backend")
                .subject(cliente.get().getUuid().toString())
                .expiresAt(now.plusSeconds(expirensIn))
                .issuedAt(now)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        //O cookie não pode ser acessado por javascript
        //Válido p/ todas as rotas
        ResponseCookie cookie = ResponseCookie.from("jwt-token", jwtValue)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofSeconds(expirensIn))
                .sameSite("Strict")
                .build();

        // 2. Retorne o cookie no cabeçalho da resposta
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse("",expirensIn));


    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Cria um cookie com o mesmo nome, mas com valor vazio e tempo de vida 0
        ResponseCookie cookie = ResponseCookie.from("jwt-token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // Expira imediatamente
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
