package com.db.dsg.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PollRequestDto {
    private String question;
    private Long groupId;
    private LocalDateTime deadline;
    private List<PollOptionDto> options;
}
