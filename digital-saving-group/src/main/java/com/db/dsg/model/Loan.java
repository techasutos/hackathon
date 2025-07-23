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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👤 The member who applied for the loan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 💰 Loan amount approved
    @Column(nullable = false)
    private BigDecimal amount;

    // 💰 Remaining amount to be repaid
    @Column(nullable = false)
    private BigDecimal remainingBalance;

    // 📝 Purpose category (e.g., business, medical)
    private String purpose;

    // 📝 Detailed description of loan purpose
    @Column(length = 1000)
    private String purposeDescription;

    // 📌 Status of the loan (Pending, Approved, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    // 📆 Date when loan was applied
    @Column(nullable = false)
    private LocalDate applicationDate = LocalDate.now();

    // 📆 Date when loan was approved
    private LocalDate approvalDate;

    // 📆 Date when member requested disbursement
    private LocalDate disbursementRequestDate;

    // 📆 Date when loan was disbursed
    private LocalDate disbursementDate;

    // 📆 Date of next scheduled repayment
    private LocalDate repaymentDate;

    // 📆 Date when the loan record was created
    private LocalDate createdDate;

    // 📆 Date by which current EMI is due
    private LocalDate dueDate;

    // ⏳ Loan tenure in months
    @Column(nullable = false)
    private Integer tenureMonths;

    // 💵 Monthly EMI amount
    @Column(nullable = false)
    private BigDecimal monthlyEmi;

    // 💵 Member's monthly income
    @Column(nullable = false)
    private BigDecimal monthlyIncome;

    // 📈 EMI paid count (optional: used to calculate remaining EMIs)
    private Integer emiPaidCount;

    // ✅ Computed field for checking if loan is fully repaid
    public boolean isFullyRepaid() {
        return remainingBalance != null && remainingBalance.compareTo(BigDecimal.ZERO) <= 0;
    }

    // ✅ Computed field to get total EMI remaining
    public int getRemainingEmiCount() {
        if (emiPaidCount == null || tenureMonths == null) return 0;
        return Math.max(0, tenureMonths - emiPaidCount);
    }
}