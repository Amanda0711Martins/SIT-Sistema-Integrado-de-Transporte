package com.logistica.customer.repository;

import com.logistica.customer.model.Customer;
import com.logistica.customer.model.Customer.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByCnpj(String cnpj);

    Optional<Customer> findByEmail(String email);

    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);

    Page<Customer> findByNameContainingIgnoreCaseAndStatus(String name, CustomerStatus status, Pageable pageable);
}