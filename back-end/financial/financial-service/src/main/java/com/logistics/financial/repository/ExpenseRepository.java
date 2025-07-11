package com.logistics.financial.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logistics.financial.model.Expense;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Page<Expense> findByType(Expense.ExpenseType type, Pageable pageable);
    List<Expense> findByExpenseDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Page<Expense> findByCategory(String category, Pageable pageable);
}