package com.sittransportadora.config;

import com.sittransportadora.model.Role;
import com.sittransportadora.model.User;
import com.sittransportadora.repository.UserRepository;
import com.sittransportadora.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class AdminInit implements CommandLineRunner {
    private RoleRepository roleRepository;
    private UserRepository clienteRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminInit(RoleRepository roleRepository, UserRepository clienteRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.roleRepository = roleRepository;
        this.clienteRepository = clienteRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var roleAdmin = roleRepository.findByName(Role.Values.ROLE_ADMIN.name());
        var roleUser = roleRepository.findByName(Role.Values.ROLE_USER.name());
        // Criar Role ADMIN se não existir
        if (roleAdmin == null) {
            Role adminRole = new Role();
            adminRole.setName(Role.Values.ROLE_ADMIN.name());
            roleAdmin = (roleRepository.save(adminRole));
        }
        if (roleUser == null) {
            Role userRole = new Role();
            userRole.setName(Role.Values.ROLE_USER.name());
            roleUser = (roleRepository.save(userRole));
        }
        //Cria conta admin para testes
        var userAdmin = clienteRepository.findByEmail("admin@admin.com");
        Role finalRoleAdmin = (Role) roleAdmin;
        userAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("Admin já existe");
                },
                () -> {
                    var cliente = new User();
                    cliente.setEmail("admin@admin.com");
                    cliente.setPassword(bCryptPasswordEncoder.encode("123"));
                    cliente.setRoles(Set.of(finalRoleAdmin)); // Corrigido: não usa null
                    clienteRepository.save(cliente);
                });

        
    }
}
