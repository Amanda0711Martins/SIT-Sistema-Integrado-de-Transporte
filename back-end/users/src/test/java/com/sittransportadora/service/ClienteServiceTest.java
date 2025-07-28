package com.sittransportadora.service;

import com.sittransportadora.model.Role;
import com.sittransportadora.repository.RoleRepository;
import com.sittransportadora.repository.UserRepository;
import com.sittransportadora.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock 
    BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    private Role role;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUuid(UUID.randomUUID());
        user.setName("João Teste");
        user.setEmail("joao@gmail.com");
        user.setPassword("SenhaForte123");
    }

    @Test
    void testSaveUser() {
        // Arrange - Preparar a simulação
        String rawPasswrod = user.getPassword();
        String encodedPassword = "encrypted_password";

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(passwordEncoder.encode(rawPasswrod)).thenReturn(encodedPassword);

        // Act - Ação
        User savedUser = userService.saveUser(user);

        // Assert - Verificar
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
        // Arrange - Preparar
        List<User> userList = Collections.singletonList(user);
        when(userRepository.findAll()).thenReturn(userList);

        // Act - Ação
        List<User> result = userService.findAll();

        // Assert - Verifica
        assertEquals(1, result.size());
        assertEquals("ROLE_USER", result.get(0).getName());
        verify(roleRepository).findAll();
    }

    @Test
    void testFindByIdFound() {
        // Arrange - Preparar
        UUID id = user.getUuid();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(id);

        assertTrue(result.isPresent());
        assertEquals("João Teste", result.get().getName());
        verify(userRepository).findById(id);
    }

    @Test
    void testFindByEmail() {
        String userEmail = user.getEmail();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail(userEmail);

        assertTrue(result.isPresent());
        assertEquals(userEmail, result.get().getEmail());
        verify(userRepository).findByEmail(userEmail);
    }

    @Test
    void testUpdateUser() {
        // Arrange: Preparamos os dados
        UUID id = user.getUuid();

        User updatedInfo = new User();
        updatedInfo.setName("João Atualizado");
        updatedInfo.setEmail("novo@email.com");
        updatedInfo.setPassword("NovaSenha456");

        String encodedNewPassword = "nova_senha_encriptada";

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(updatedInfo.getPassword())).thenReturn(encodedNewPassword);

        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act: Executamos o método que queremos testar
        User result = userService.updateUser(id, updatedInfo);

        // Assert: Verificamos se o resultado está correto
        assertEquals("João Atualizado", result.getName());
        assertEquals("novo@email.com", result.getEmail());
        assertEquals(encodedNewPassword, result.getPassword()); 
        assertNotNull(result.getAlterDate()); 
        // Verificamos se os mocks foram chamados como esperado
        verify(userRepository).findById(id);
        verify(userRepository).save(user);
        verify(passwordEncoder).encode(updatedInfo.getPassword());
    }

    @Test
    void testDeleteRole_ThrowsException_WhenRoleNotFound() {
        Long roleId = 99L;
        when(roleRepository.existsById(roleId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.deleteRole(roleId);
        });

        assertEquals("Role with id 99 not found.", exception.getMessage());
        verify(roleRepository, never()).deleteById(anyLong());
    }
}
