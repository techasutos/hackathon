package com.db.dsg.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanAuditLogDto {
    private Long id;
    private String performedBy;
    private String description;
    private String status;
    private LocalDateTime timestamp;
}
