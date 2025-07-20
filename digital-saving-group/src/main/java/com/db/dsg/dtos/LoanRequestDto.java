package com.db.dsg.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanRequestDto {
    @NotNull
    private BigDecimal amount;

    private String purpose;

    @NotNull
    private Integer tenureMonths;

    @NotNull
    private BigDecimal monthlyEmi;

    @NotNull
    private BigDecimal monthlyIncome;
}
