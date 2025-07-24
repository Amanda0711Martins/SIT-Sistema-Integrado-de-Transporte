package com.sittransportadora.service;

import com.sittransportadora.model.User;
import com.sittransportadora.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUuid(UUID.randomUUID());
        user.setName("João Teste");
        user.setPassword("SenhaForte123");
    }

    @Test
    void testSaveUser() {
        //Arrange - Preparar a simulação
        String rawPasswrod = user.getPassword();
        String encodedPassword = "encrypted_password";

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(passwordEncoder.encode(rawPasswrod)).thenReturn(encodedPassword);

        //Act - Ação
        User savedUser = userService.saveUser(user);

        //Assert - Verificar
        assertNotNull(savedUser.getCreateDate());
        assertEquals(encodedPassword, savedUser.getPassword());
        assertNotEquals(encodedPassword, savedUser.getPassword());
        assertNotNull(savedUser.getRoles());
        assertEquals(1, savedUser.getRoles().size());

        verify(userRepository).save(user);
        verify(passwordEncoder).encode(rawPasswrod);
    }

    @Test
    void testFindAll() {
        //Arrange - Preparar
        List<User> userList = Collections.singletonList(user);
        when(userRepository.findAll()).thenReturn(userList);

        // Act - Ação 
        List<User> result = userService.findAll();

        //Assert - Verifica
        assertEquals(1, result.size());
        assertEquals("João Teste", result.get(0).getName());
        verify(userRepository).findAll();
    }

    @Test
    void testFindByIdFound() {
        //Arrange - Preparar
        UUID id = user.getUuid();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(id);

        assertTrue(result.isPresent());
        assertEquals("João Teste", result.get().getName());
        verify(userRepository).findById(id);
    }

    @Test
    void testUpdateUser() {
        UUID id = user.getUuid();
        User updateUser = new User();
        updateUser.setAddress("");


        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User updated = userService.updateUser(id, user);

        verify(userRepository).save(user);
        assertEquals("João Teste", updated.getName());

    }

    @Test
    void testDeleteUser() {
        UUID id = user.getUuid();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.deleteUser(id);

        verify(userRepository).delete(user);
    }
}        

