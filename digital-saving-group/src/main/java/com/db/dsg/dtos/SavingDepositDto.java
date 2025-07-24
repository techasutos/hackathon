package com.db.dsg.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingDepositDto {
    private Long id;
    private BigDecimal amount;
    private String remarks;
    private LocalDateTime date;
    private String memberName;
}
