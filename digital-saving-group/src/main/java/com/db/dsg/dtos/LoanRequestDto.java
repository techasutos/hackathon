package com.db.dsg.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRequestDto {
    private BigDecimal amount;
    private String purpose;
}
