package com.db.dsg.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDTO {
    private Long id;

    @NotBlank
    private String name;

    private String projectTag;
    private LocalDate createdDate;

    private List<Long> memberIds; // IDs of members (optional for read)
}
