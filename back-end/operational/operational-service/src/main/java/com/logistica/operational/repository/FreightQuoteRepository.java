package com.logistica.operational.repository;

import com.logistica.operational.models.FreightQuote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreightQuoteRepository extends JpaRepository<FreightQuote, Long> {}