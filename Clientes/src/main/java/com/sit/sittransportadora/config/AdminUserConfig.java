package com.sit.sittransportadora.config;

import com.sit.sittransportadora.domain.Cliente;
import com.sit.sittransportadora.domain.Role;
import com.sit.sittransportadora.repository.ClienteRepository;
import com.sit.sittransportadora.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {
    private RoleRepository roleRepository;
    private ClienteRepository clienteRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminUserConfig(RoleRepository roleRepository, ClienteRepository clienteRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.roleRepository = roleRepository;
        this.clienteRepository = clienteRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());

        // Criar Role ADMIN se não existir
        if (roleAdmin == null) {
            Role adminRole = new Role();
            adminRole.setName(Role.Values.ADMIN.name());
            roleAdmin = roleRepository.save(adminRole);
        }

        var userAdmin = clienteRepository.findByEmail("admin@admin.com");

        Role finalRoleAdmin = (Role) roleAdmin;

        userAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("Admin já existe");
                },
                () -> {
                    var cliente = new Cliente();
                    cliente.setEmail("admin@admin.com");
                    cliente.setPassword(bCryptPasswordEncoder.encode("123"));
                    cliente.setRoles(Set.of(finalRoleAdmin)); // Corrigido: não usa null
                    clienteRepository.save(cliente);
                }
        );
    }
}
