package com.db.dsg.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingSummaryResponse {
    private BigDecimal totalDeposited;
    private Long numberOfDeposits;
    private LocalDateTime lastUpdated;
}
