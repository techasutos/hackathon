package com.db.dsg.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollDto {
    private Long id;
    private String question;
    private Long groupId;
    private String groupName;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private boolean closed;
}
