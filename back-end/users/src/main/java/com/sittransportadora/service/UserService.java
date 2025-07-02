package com.sittransportadora.service;

import com.sittransportadora.model.Role;
import com.sittransportadora.model.User;
import com.sittransportadora.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User saveUser(User user) {
        user.setCreateDate(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = new Role();
        role.setName(Role.Values.ROLE_USER.name());
        user.setRoles(Set.of(role));
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

    public User updateUser(UUID id, User userAtualizado) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(userAtualizado.getName());
                    existingUser.setEmail(userAtualizado.getEmail());
                    existingUser.setPassword(passwordEncoder.encode(userAtualizado.getPassword()));
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        userRepository.delete(user);
    }
}
