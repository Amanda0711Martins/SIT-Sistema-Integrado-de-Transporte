package com.sittransportadora.service;

import com.sittransportadora.model.Role;
import com.sittransportadora.model.User;
import com.sittransportadora.repository.ClienteRepository;
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
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User saveCliente(User user) {
        user.setCreateDate(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = new Role();
        role.setName(Role.Values.ROLE_USER.name());
        user.setRoles(Set.of(role));
        return clienteRepository.save(user);
    }

    public List<User> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<User> findById(UUID id) {
        return clienteRepository.findById(id);

    }

    public Optional<User> findByEmail(String email) {
        Optional<User> cliente = clienteRepository.findByEmail(email);
        return cliente;
    }

    public User updateCliente(UUID id, User userAtualizado) {
        return clienteRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(userAtualizado.getName());
                    existingUser.setEmail(userAtualizado.getEmail());
                    existingUser.setPassword(passwordEncoder.encode(userAtualizado.getPassword()));
                    return clienteRepository.save(existingUser);
                })
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public void deleteCliente(UUID id) {
        User cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        clienteRepository.delete(cliente);
    }
}
