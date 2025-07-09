package com.logistics.financial.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String invoiceNumber;

    @NotNull
    private Long clientId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private LocalDateTime issueDate;

    private LocalDateTime dueDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    private String fiscalKey;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum InvoiceStatus {
        DRAFT, ISSUED, PAID, CANCELLED, OVERDUE
    }
}