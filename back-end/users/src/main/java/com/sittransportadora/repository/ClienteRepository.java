package com.sittransportadora.repository;

import com.sittransportadora.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<User, UUID> {
    User findByCpf(String email);
    Optional<User> findByEmail(String email);

}
