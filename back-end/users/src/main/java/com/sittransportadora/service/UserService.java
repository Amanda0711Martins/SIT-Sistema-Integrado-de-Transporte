package com.sittransportadora.service;

import com.sittransportadora.controller.dto.userdto.UserDTO;
import com.sittransportadora.model.Role;
import com.sittransportadora.model.User;
import com.sittransportadora.repository.RoleRepository;
import com.sittransportadora.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public User saveUser(User user) {
        user.setCreateDate(LocalDateTime.now());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Role role = new Role();
        role.setName(Role.Values.ROLE_USER.name());
        user.setRoles(Set.of(role));
        return userRepository.save(user);
    }

    @Transactional
    public User signupNewUser(UserDTO userDTO) {
        Role defaultRole = roleRepository.findByName(Role.Values.ROLE_USER.name())
            .orElseThrow(() -> new RuntimeException("Role USER não encontrada."));

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        user.setRoles(Set.of(defaultRole));

        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);

    }

    public Optional<User> findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user;
    }

    @Transactional
    public User updateUser(UUID id, User userAtualizado) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(userAtualizado.getName());
                    existingUser.setEmail(userAtualizado.getEmail());
                    existingUser.setPassword(bCryptPasswordEncoder.encode(userAtualizado.getPassword()));
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        userRepository.delete(user);
    }
}
