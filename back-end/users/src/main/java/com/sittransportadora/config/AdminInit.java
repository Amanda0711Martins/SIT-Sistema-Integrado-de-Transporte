package com.sittransportadora.config;

import com.sittransportadora.model.Role;
import com.sittransportadora.model.User;
import com.sittransportadora.repository.RoleRepository;
import com.sittransportadora.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Configuration
public class AdminInit implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminInit(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // --- CRIAÇÃO DAS ROLES ---
        // 1. Verifica se a role ADMIN não existe usando .isEmpty()
        if (roleRepository.findByName(Role.Values.ROLE_ADMIN.name()).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(Role.Values.ROLE_ADMIN.name());
            roleRepository.save(adminRole);
            System.out.println("Role ADMIN criada.");
        }

        // 2. Verifica se a role USER não existe usando .isEmpty()
        if (roleRepository.findByName(Role.Values.ROLE_USER.name()).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(Role.Values.ROLE_USER.name());
            roleRepository.save(userRole);
            System.out.println("Role USER criada.");
        }

        // --- CRIAÇÃO DO USUÁRIO ADMIN ---
        // 3. Verifica se o usuário admin já existe
        userRepository.findByEmail("admin@admin.com").ifPresentOrElse(
            user -> System.out.println("Usuário admin já existe."),
            () -> {
                System.out.println("Criando usuário admin...");
                User adminUser = new User();
                adminUser.setEmail("admin@admin.com");
                adminUser.setPassword(bCryptPasswordEncoder.encode("123"));

                // 4. Busca a role ADMIN do banco e "desembrulha" o Optional
                Role adminRole = roleRepository.findByName(Role.Values.ROLE_ADMIN.name())
                                               .orElseThrow(() -> new RuntimeException("Erro fatal: Role ADMIN não encontrada após a criação."));

                adminUser.setRoles(Set.of(adminRole));
                userRepository.save(adminUser);
                System.out.println("Usuário admin criado com sucesso.");
            }
        );
    }
}