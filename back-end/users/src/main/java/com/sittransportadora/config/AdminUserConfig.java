package com.sittransportadora.config;

import com.sittransportadora.model.Cliente;
import com.sittransportadora.model.Role;
import com.sittransportadora.repository.ClienteRepository;
import com.sittransportadora.repository.RoleRepository;
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

    public AdminUserConfig(RoleRepository roleRepository, ClienteRepository clienteRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.roleRepository = roleRepository;
        this.clienteRepository = clienteRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Criar Role ADMIN se não existir
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());
        if (roleAdmin == null) {
            Role adminRole = new Role();
            adminRole.setName(Role.Values.ADMIN.name());
            roleAdmin = roleRepository.save(adminRole);
        }
        //Cria conta admin para testes
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
                });

        // Criar Role USER se não existir
        var userDefault = roleRepository.findByName(Role.Values.USER.name());
        if (userDefault == null) {
            Role user = new Role();
            user.setName(Role.Values.ADMIN.name());
            userDefault = roleRepository.save(user);
        }
    }
}
