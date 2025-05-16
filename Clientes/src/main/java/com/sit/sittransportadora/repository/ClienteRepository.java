package com.sit.sittransportadora.repository;

import com.sit.sittransportadora.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    Cliente findByCpf(String email);
    Optional<Cliente> findByEmail(String email);

}
