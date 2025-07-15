package com.db.dsg.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingDto {
    private Long id;
    private Long groupId;
    private LocalDate date;
    private String agenda;
    private List<AttendanceDto> attendance; // optional
}
