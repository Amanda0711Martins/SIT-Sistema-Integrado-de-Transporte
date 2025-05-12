package com.sit.sittransportadora.repository;

import com.sit.sittransportadora.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByCpf(String email);
    Cliente findByEmail(String email);

}
