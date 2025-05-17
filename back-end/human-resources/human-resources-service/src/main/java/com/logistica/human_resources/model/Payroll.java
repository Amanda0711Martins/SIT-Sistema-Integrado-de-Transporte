// Payroll.java
package com.logistics.HumanResources.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "payrolls")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "year_month", nullable = false)
    private YearMonth yearMonth;

    @Column(name = "gross_salary", nullable = false)
    private BigDecimal grossSalary;

    @Column(name = "inss_deduction", nullable = false)
    private BigDecimal inssDeduction;

    @Column(name = "income_tax_deduction", nullable = false)
    private BigDecimal incomeTaxDeduction;

    @Column(name = "other_deductions")
    private BigDecimal otherDeductions;

    @Column(name = "bonuses")
    private BigDecimal bonuses;

    @Column(name = "net_salary", nullable = false)
    private BigDecimal netSalary;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "processed_at", nullable = false)
    private LocalDate processedAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PayrollStatus status;

    public enum PayrollStatus {
        DRAFT, PROCESSED, PAID, CANCELED
    }
}