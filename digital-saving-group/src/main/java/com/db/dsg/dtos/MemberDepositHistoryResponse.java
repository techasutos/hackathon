package com.db.dsg.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MemberDepositHistoryResponse {
    private String memberName;
    private List<SavingDepositDto> deposits;
}
