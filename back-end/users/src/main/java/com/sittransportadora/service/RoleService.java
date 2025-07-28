package com.sittransportadora.service;

import com.sittransportadora.model.Role;
import com.sittransportadora.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role saveRole(Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new IllegalArgumentException("Role with name " + role.getName() + " already exists.");
        }
        return roleRepository.save(role);
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Role with id " + id + " not found.");
        }
        roleRepository.deleteById(id);
    }
    
}
