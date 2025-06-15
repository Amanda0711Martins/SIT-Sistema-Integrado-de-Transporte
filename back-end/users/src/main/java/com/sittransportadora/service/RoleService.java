package com.sittransportadora.service;

import com.sittransportadora.model.Role;
import com.sittransportadora.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Optional<Role> findByName(String name){
        Role role = roleRepository.findByName(name);
        if(role != null)
        {
            return Optional.of(role);
        }
        return Optional.empty();
    }
}
