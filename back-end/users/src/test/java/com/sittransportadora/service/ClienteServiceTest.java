package com.sittransportadora.service;

import com.sittransportadora.model.Role;
import com.sittransportadora.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role;

    @BeforeEach
    void setup() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
    }

    @Test
    void testSaveRole_Success() {
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role savedRole = roleService.saveRole(role);

        assertNotNull(savedRole);
        assertEquals("ROLE_USER", savedRole.getName());
        verify(roleRepository).findByName("ROLE_USER");
        verify(roleRepository).save(role);
    }

    @Test
    void testSaveRole_ThrowsException_WhenRoleExists() {
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.saveRole(role);
        });

        assertEquals("Role with name ROLE_USER already exists.", exception.getMessage());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testFindAllRoles() {
        List<Role> roleList = Collections.singletonList(role);
        when(roleRepository.findAll()).thenReturn(roleList);

        List<Role> result = roleService.findAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ROLE_USER", result.get(0).getName());
        verify(roleRepository).findAll();
    }

    @Test
    void testFindRoleByName_WhenRoleExists() {
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        Optional<Role> result = roleService.findRoleByName("ROLE_USER");

        assertTrue(result.isPresent());
        assertEquals("ROLE_USER", result.get().getName());
        verify(roleRepository).findByName("ROLE_USER");
    }

    @Test
    void testFindRoleByName_WhenRoleNotFound() {
        when(roleRepository.findByName("ROLE_NONEXISTENT")).thenReturn(Optional.empty());

        Optional<Role> result = roleService.findRoleByName("ROLE_NONEXISTENT");

        assertFalse(result.isPresent());
        verify(roleRepository).findByName("ROLE_NONEXISTENT");
    }
    
    @Test
    void testDeleteRole_Success() {
        Long roleId = 1L;
        when(roleRepository.existsById(roleId)).thenReturn(true);
        doNothing().when(roleRepository).deleteById(roleId);

        roleService.deleteRole(roleId);

        verify(roleRepository).deleteById(roleId);
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
