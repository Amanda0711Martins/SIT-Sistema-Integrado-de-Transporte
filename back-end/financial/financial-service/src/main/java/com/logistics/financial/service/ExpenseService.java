package com.logistics.financial.service;

import com.logistics.financial.dto.ExpenseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseService {
    ExpenseDTO createExpense(ExpenseDTO expenseDTO);
    ExpenseDTO getExpenseById(Long id);
    Page<ExpenseDTO> getAllExpenses(Pageable pageable);
    ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO);
    void deleteExpense(Long id);
    Page<ExpenseDTO> getExpensesByType(String type, Pageable pageable);
    List<ExpenseDTO> getExpensesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    Page<ExpenseDTO> getExpensesByCategory(String category, Pageable pageable);
}