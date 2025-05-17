package com.logistics.financial.service;

import com.logistics.financial.dto.ExpenseDTO;
import com.logistics.financial.exception.ResourceNotFoundException;
import com.logistics.financial.mapper.ExpenseMapper;
import com.logistics.financial.model.Expense;
import com.logistics.financial.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private Expense expense;
    private ExpenseDTO expenseDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        expense = new Expense();
        expense.setId(1L);
        expense.setDescription("Test Expense");
        expense.setAmount(BigDecimal.valueOf(100.00));
        expense.setType(Expense.ExpenseType.FUEL);
        expense.setCategory("Vehicle");
        expense.setExpenseDate(now);
        expense.setCreatedAt(now);
        expense.setUpdatedAt(now);

        expenseDTO = new ExpenseDTO();
        expenseDTO.setId(1L);
        expenseDTO.setDescription("Test Expense");
        expenseDTO.setAmount(BigDecimal.valueOf(100.00));
        expenseDTO.setType(Expense.ExpenseType.FUEL);
        expenseDTO.setCategory("Vehicle");
        expenseDTO.setExpenseDate(now);
        expenseDTO.setCreatedAt(now);
        expenseDTO.setUpdatedAt(now);
    }

    @Test
    void createExpense_ShouldReturnCreatedExpense() {
        // Arrange
        when(expenseMapper.toEntity(any(ExpenseDTO.class))).thenReturn(expense);
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(expenseMapper.toDto(any(Expense.class))).thenReturn(expenseDTO);

        // Act
        ExpenseDTO result = expenseService.createExpense(expenseDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expenseDTO.getId());
        assertThat(result.getDescription()).isEqualTo(expenseDTO.getDescription());
        assertThat(result.getAmount()).isEqualTo(expenseDTO.getAmount());

        verify(expenseMapper).toEntity(expenseDTO);
        verify(expenseRepository).save(expense);
        verify(expenseMapper).toDto(expense);
    }

    @Test
    void getExpenseById_WhenExpenseExists_ShouldReturnExpense() {
        // Arrange
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(expense));
        when(expenseMapper.toDto(any(Expense.class))).thenReturn(expenseDTO);

        // Act
        ExpenseDTO result = expenseService.getExpenseById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expenseDTO.getId());

        verify(expenseRepository).findById(1L);
        verify(expenseMapper).toDto(expense);
    }

    @Test
    void getExpenseById_WhenExpenseDoesNotExist_ShouldThrowException() {
        // Arrange
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> expenseService.getExpenseById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Expense not found with id: 1");

        verify(expenseRepository).findById(1L);
        verifyNoInteractions(expenseMapper);
    }

    @Test
    void getAllExpenses_ShouldReturnPageOfExpenses() {
        // Arrange
        Page<Expense> expensePage = new PageImpl<>(Collections.singletonList(expense));
        when(expenseRepository.findAll(any(Pageable.class))).thenReturn(expensePage);
        when(expenseMapper.toDto(any(Expense.class))).thenReturn(expenseDTO);

        // Act
        Page<ExpenseDTO> result = expenseService.getAllExpenses(Pageable.unpaged());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(expenseDTO.getId());

        verify(expenseRepository).findAll(Pageable.unpaged());
        verify(expenseMapper).toDto(expense);
    }

    @Test
    void updateExpense_WhenExpenseExists_ShouldReturnUpdatedExpense() {
        // Arrange
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.of(expense));
        when(expenseMapper.toEntity(any(ExpenseDTO.class))).thenReturn(expense);
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(expenseMapper.toDto(any(Expense.class))).thenReturn(expenseDTO);

        // Act
        ExpenseDTO result = expenseService.updateExpense(1L, expenseDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(expenseDTO.getId());

        verify(expenseRepository).findById(1L);
        verify(expenseMapper).toEntity(expenseDTO);
        verify(expenseRepository).save(expense);
        verify(expenseMapper).toDto(expense);
    }

    @Test
    void updateExpense_WhenExpenseDoesNotExist_ShouldThrowException() {
        // Arrange
        when(expenseRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> expenseService.updateExpense(1L, expenseDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Expense not found with id: 1");

        verify(expenseRepository).findById(1L);
        verifyNoInteractions(expenseMapper);
    }

    @Test
    void deleteExpense_WhenExpenseExists_ShouldDeleteExpense() {
        // Arrange
        when(expenseRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(expenseRepository).deleteById(anyLong());

        // Act
        expenseService.deleteExpense(1L);

        // Assert
        verify(expenseRepository).existsById(1L);
        verify(expenseRepository).deleteById(1L);
    }

    @Test
    void deleteExpense_WhenExpenseDoesNotExist_ShouldThrowException() {
        // Arrange
        when(expenseRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> expenseService.deleteExpense(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Expense not found with id: 1");

        verify(expenseRepository).existsById(1L);
        verify(expenseRepository, never()).deleteById(anyLong());
    }

    @Test
    void getExpensesByType_ShouldReturnExpensesOfSpecificType() {
        // Arrange
        Page<Expense> expensePage = new PageImpl<>(Collections.singletonList(expense));
        when(expenseRepository.findByType(any(Expense.ExpenseType.class), any(Pageable.class))).thenReturn(expensePage);
        when(expenseMapper.toDto(any(Expense.class))).thenReturn(expenseDTO);

        // Act
        Page<ExpenseDTO> result = expenseService.getExpensesByType("FUEL", Pageable.unpaged());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getType()).isEqualTo(Expense.ExpenseType.FUEL);

        verify(expenseRepository).findByType(Expense.ExpenseType.FUEL, Pageable.unpaged());
        verify(expenseMapper).toDto(expense);
    }

    @Test
    void getExpensesByDateRange_ShouldReturnExpensesInDateRange() {
        // Arrange
        LocalDateTime startDate = now.minusDays(1);
        LocalDateTime endDate = now.plusDays(1);
        List<Expense> expenses = Collections.singletonList(expense);
        when(expenseRepository.findByExpenseDateBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(expenses);
        when(expenseMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(expenseDTO));

        // Act
        List<ExpenseDTO> result = expenseService.getExpensesByDateRange(startDate, endDate);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(expenseDTO.getId());

        verify(expenseRepository).findByExpenseDateBetween(startDate, endDate);
        verify(expenseMapper).toDtoList(expenses);
    }

    @Test
    void getExpensesByCategory_ShouldReturnExpensesOfSpecificCategory() {
        // Arrange
        Page<Expense> expensePage = new PageImpl<>(Collections.singletonList(expense));
        when(expenseRepository.findByCategory(anyString(), any(Pageable.class))).thenReturn(expensePage);
        when(expenseMapper.toDto(any(Expense.class))).thenReturn(expenseDTO);

        // Act
        Page<ExpenseDTO> result = expenseService.getExpensesByCategory("Vehicle", Pageable.unpaged());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo("Vehicle");

        verify(expenseRepository).findByCategory("Vehicle", Pageable.unpaged());
        verify(expenseMapper).toDto(expense);
    }
}