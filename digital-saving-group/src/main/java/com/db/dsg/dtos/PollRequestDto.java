package com.db.dsg.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PollRequestDto {
    private String question;
    private Long groupId;
    private LocalDateTime deadline; // ✅ NEW FIELD
}
