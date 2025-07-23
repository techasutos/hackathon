package com.db.dsg.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {
    private Long id;
    private BigDecimal amount;
    private BigDecimal remainingBalance;
    private String status;
    private String purpose;
    private String purposeDescription;
    private LocalDate applicationDate;
    private LocalDate approvalDate;
    private LocalDate disbursementDate;
    private LocalDate repaymentDate;
    private String memberName;
    private Long memberId;
    private LocalDate createdDate;
    private Long groupId;
    private BigDecimal monthlyIncome;
    private Integer tenureMonths;
    private BigDecimal monthlyEmi;

}
