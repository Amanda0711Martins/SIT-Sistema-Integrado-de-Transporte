package com.sittransportadora.service;

import com.sittransportadora.model.User;
import com.sittransportadora.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

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
    }

    @Test
    void testSaveUser() {
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User saved = userService.saveUser(user);

        assertNotNull(saved.getCreateDate());
        verify(userRepository).save(user);
    }

    @Test
    void testFindAll() {
        List<User> lista = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(lista);

        List<User> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals("João Teste", result.get(0).getName());
    }

    @Test
    void testFindByIdFound() {
        UUID id = user.getUuid();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(id);

        assertTrue(result.isPresent());
        assertEquals("João Teste", result.get().getName());
    }

    @Test
    void testUpdateUser() {
        UUID id = user.getUuid();
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

