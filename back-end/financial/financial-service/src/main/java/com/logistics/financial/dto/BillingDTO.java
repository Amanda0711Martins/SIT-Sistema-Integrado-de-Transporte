package com.logistics.financial.dto;

import com.logistics.financial.model.Billing.BillingStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingDTO {
    private Long id;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    private String clientName;

    @NotBlank(message = "Billing number is required")
    private String billingNumber;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Issue date is required")
    private LocalDateTime issueDate;

    @NotNull(message = "Due date is required")
    private LocalDateTime dueDate;

    @NotNull(message = "Status is required")
    private BillingStatus status;

    private String barCode;

    private String paymentMethod;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}