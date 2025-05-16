package com.sit.sittransportadora.repository;

import com.sit.sittransportadora.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Object findByName(String name);
}
