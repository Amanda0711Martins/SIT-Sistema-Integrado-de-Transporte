package com.logistics.financial.service;

import com.logistics.financial.dto.ExpenseDTO;
import com.logistics.financial.exception.ResourceNotFoundException;
import com.logistics.financial.mapper.ExpenseMapper;
import com.logistics.financial.model.Expense;
import com.logistics.financial.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    @Override
    @Transactional
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        log.info("Creating new expense: {}", expenseDTO);
        Expense expense = expenseMapper.toEntity(expenseDTO);
        Expense savedExpense = expenseRepository.save(expense);
        return expenseMapper.toDto(savedExpense);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseDTO getExpenseById(Long id) {
        log.info("Getting expense with id: {}", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        return expenseMapper.toDto(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getAllExpenses(Pageable pageable) {
        log.info("Getting all expenses");
        Page<Expense> expenses = expenseRepository.findAll(pageable);
        return expenses.map(expenseMapper::toDto);
    }

    @Override
    @Transactional
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        log.info("Updating expense with id: {}", id);
        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        Expense updatedExpense = expenseMapper.toEntity(expenseDTO);
        updatedExpense.setId(id);
        updatedExpense.setCreatedAt(existingExpense.getCreatedAt());

        Expense savedExpense = expenseRepository.save(updatedExpense);
        return expenseMapper.toDto(savedExpense);
    }

    @Override
    @Transactional
    public void deleteExpense(Long id) {
        log.info("Deleting expense with id: {}", id);
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getExpensesByType(String type, Pageable pageable) {
        log.info("Getting expenses by type: {}", type);
        Expense.ExpenseType expenseType = Expense.ExpenseType.valueOf(type);
        Page<Expense> expenses = expenseRepository.findByType(expenseType, pageable);
        return expenses.map(expenseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getExpensesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting expenses between {} and {}", startDate, endDate);
        List<Expense> expenses = expenseRepository.findByExpenseDateBetween(startDate, endDate);
        return expenseMapper.toDtoList(expenses);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDTO> getExpensesByCategory(String category, Pageable pageable) {
        log.info("Getting expenses by category: {}", category);
        Page<Expense> expenses = expenseRepository.findByCategory(category, pageable);
        return expenses.map(expenseMapper::toDto);
    }
}