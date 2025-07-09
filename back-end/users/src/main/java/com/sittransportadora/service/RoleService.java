package com.sittransportadora.service;

import com.sittransportadora.model.Role;
import com.sittransportadora.repository.RoleRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id) ;
    }

   public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("ERRO: A Role '" + name + "' não foi encontrada no banco de dados."));
    }

    public void deleteRole(Long id) {
        roleRepository.findById(id).ifPresent(roleRepository::delete);
    }
}
