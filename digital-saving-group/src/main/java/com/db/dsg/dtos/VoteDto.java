package com.db.dsg.dtos;

import com.db.dsg.model.VoteOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteDto {
    private Long id;
    private Long pollId;
    private Long memberId;
    private VoteOption option;
    private LocalDateTime timestamp;
}
