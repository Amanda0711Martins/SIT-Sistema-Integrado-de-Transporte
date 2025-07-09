package com.sittransportadora;

import com.sittransportadora.model.User;
import com.sittransportadora.repository.RoleRepository;
import com.sittransportadora.repository.UserRepository;
import com.sittransportadora.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientesApplicationTests {

    @Mock
    private UserRepository clienteRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void quandoSalvarCliente_deveEncodarSenhaESalvarNoRepositorio() {
        User userToSave = new User();
        userToSave.setEmail("test@email.com");
        userToSave.setPassword("rawPassword123");

        when(passwordEncoder.encode("rawPassword123")).thenReturn("encodedPassword");
        when(clienteRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.saveUser(userToSave);

        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());

        verify(passwordEncoder, times(1)).encode("rawPassword123");
        verify(clienteRepository, times(1)).save(userToSave);
    }

    @Test
    void quandoBuscarPorEmailInexistente_deveRetornarOptionalVazio() {
        String email = "notfound@email.com";
        when(clienteRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.findByEmail(email);

        assertTrue(foundUser.isEmpty());
        verify(clienteRepository, times(1)).findByEmail(email);
    }
}
