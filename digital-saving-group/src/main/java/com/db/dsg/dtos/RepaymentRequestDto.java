package com.db.dsg.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RepaymentRequestDto {
    private BigDecimal amount;
}
