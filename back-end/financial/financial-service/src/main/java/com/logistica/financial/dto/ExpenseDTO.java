package com.logistica.financial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ExpenseDTO {
    private Long id;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Expense type is required")
    private ExpenseType type;

    private String category;

    @NotNull(message = "Expense date is required")
    private LocalDateTime expenseDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}