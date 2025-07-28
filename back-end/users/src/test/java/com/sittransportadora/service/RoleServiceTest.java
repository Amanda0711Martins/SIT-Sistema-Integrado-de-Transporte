package com.sittransportadora.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sittransportadora.model.Role;
import com.sittransportadora.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    
    
    @Mock
    private RoleRepository roleRepository;

    private Role role;

    @BeforeEach
    void setup(){
        role = new Role();
        long random = new Random().nextLong(10);
        role.setId(random);
        role.setName("ROLE_USER");
    }

    @Test
    void testSaveRole(){
        when(roleRepository.save(any(Role.class))).thenAnswer(i -> i.getArgument(0));
        
        Role roleSaved = roleRepository.save(role);

        assertNotNull(roleSaved.getName());
        assertNotNull(roleSaved);
        verify(roleRepository).save(role);
    }

    
}
