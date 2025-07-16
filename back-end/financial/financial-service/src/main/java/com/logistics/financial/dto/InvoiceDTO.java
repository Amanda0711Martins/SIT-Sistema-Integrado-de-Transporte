package com.logistics.financial.dto;

import com.logistics.financial.model.Invoice.InvoiceStatus;

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
public class InvoiceDTO {
    private Long id;

    @NotBlank(message = "Invoice number is required")
    private String invoiceNumber;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    private String clientName;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Issue date is required")
    private LocalDateTime issueDate;

    private LocalDateTime dueDate;

    @NotNull(message = "Status is required")
    private InvoiceStatus status;

    private String fiscalKey;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}