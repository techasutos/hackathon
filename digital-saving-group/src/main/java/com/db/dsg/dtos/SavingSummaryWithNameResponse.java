package com.db.dsg.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SavingSummaryWithNameResponse {
    private String memberName;
    private SavingSummaryResponse summary;
}
