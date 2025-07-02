package com.sittransportadora.controller;

import com.sittransportadora.model.Role;
import com.sittransportadora.controller.dto.userdto.AuthStatus;
import com.sittransportadora.controller.dto.userdto.LoginRequest;
import com.sittransportadora.controller.dto.userdto.LoginResponse;
import com.sittransportadora.controller.dto.userdto.UserDTO;
import com.sittransportadora.controller.dto.userdto.UserResponseRoleDTO;
import com.sittransportadora.model.User;
import com.sittransportadora.service.UserService;
import com.sittransportadora.service.RoleService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class TokenController {

    private final JwtEncoder jwtEncoder;
    private final RoleService roleService;
    private UserService clienteService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public TokenController(JwtEncoder jwtEncoder, UserService clienteService, BCryptPasswordEncoder bCryptPasswordEncoder, RoleService roleService) {
        this.jwtEncoder = jwtEncoder;
        this.clienteService = clienteService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleService = roleService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        Role userRole = roleService.findByName(Role.Values.ROLE_USER.name())
                .orElseThrow(() -> new RuntimeException("Erro: Role não encontrada."));
        System.out.println("ROLE ID: " + userRole.getId()); // ⚠️ DEVE imprimir um número
        user.setRoles(Set.of(userRole));
        String encodedPassword = bCryptPasswordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);

        User userSalvo = clienteService.saveUser(user);
        userDTO.setPassword("");
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        var cliente = clienteService.findByEmail(loginRequest.email());
        if(cliente.isEmpty() || !cliente.get().isLoginCorrect(loginRequest,bCryptPasswordEncoder)){
            throw new BadCredentialsException("User or password is invalid");
        }

        var now = Instant.now();
        //5 minutos
        var expirensIn = 300L;

        var scopes = cliente.get().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));


        var claims = JwtClaimsSet.builder()
                .issuer("Backend")
                .subject(cliente.get().getUuid().toString())
                .claim("roles", scopes)
                .expiresAt(now.plusSeconds(expirensIn))
                .issuedAt(now)
                .claim("scope", scopes)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        //O cookie não pode ser acessado por javascript
        //Válido p/ todas as rotas
        ResponseCookie cookie = ResponseCookie.from("jwt-token", jwtValue)
                .httpOnly(true)
//                .secure(true) exige https em produção
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
    @GetMapping("/status")
    public ResponseEntity<AuthStatus> getAuthStatus(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(new AuthStatus(false, null));
        }

        // O 'subject' do nosso token é o UUID do usuário.
        String userUuid = authentication.getName();

        // Busca o usuário completo no banco de dados para ter os dados atualizados.
        return this.clienteService.findById(UUID.fromString(userUuid))
                .map(user -> {
                    // Converte a entidade User para um DTO seguro
                    UserResponseRoleDTO userDTO = UserResponseRoleDTO.fromEntity(user);
                    // Cria a resposta de sucesso
                    AuthStatus response = new AuthStatus(true, userDTO);
                    return ResponseEntity.ok(response);
                })
                // Se o UUID do token não corresponder a nenhum usuário no banco, retorna não autenticado.
                .orElse(ResponseEntity.ok(new AuthStatus(false, null)));
    }
}
