package com.db.dsg.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Loan {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private BigDecimal amount;

    private BigDecimal remainingBalance;

    private String purpose;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    private LocalDate applicationDate = LocalDate.now();

    private LocalDate approvalDate;

    private LocalDate disbursementDate;

    private LocalDate repaymentDate;

    private LocalDate createdDate;

    private LocalDate dueDate;

    private Integer tenureMonths;

    private BigDecimal monthlyEmi;

    private BigDecimal monthlyIncome;
}