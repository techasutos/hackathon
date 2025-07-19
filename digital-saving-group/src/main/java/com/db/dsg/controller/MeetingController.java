package com.db.dsg.controller;

import com.db.dsg.dtos.AttendanceDto;
import com.db.dsg.dtos.CreateMeetingRequest;
import com.db.dsg.dtos.MeetingDto;
import com.db.dsg.model.Attendance;
import com.db.dsg.model.Meeting;
import com.db.dsg.model.MemberUser;
import com.db.dsg.service.impl.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService service;
    private final ModelMapper modelMapper;

    @PostMapping
    @PreAuthorize("hasRole('PRESIDENT') or hasRole('ADMIN')")
    public ResponseEntity<MeetingDto> createMeeting(@RequestBody @Valid CreateMeetingRequest request) {
        Meeting created = service.createMeeting(request);
        return ResponseEntity.ok(modelMapper.map(created, MeetingDto.class));
    }

    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasRole('PRESIDENT') or hasRole('ADMIN') or hasRole('TREASURER')")
    public ResponseEntity<List<MeetingDto>> getMeetingsByGroup(@PathVariable Long groupId) {
        List<MeetingDto> meetings = service.getMeetingsByGroup(groupId)
                .stream()
                .map(meeting -> modelMapper.map(meeting, MeetingDto.class))
                .toList();
        return ResponseEntity.ok(meetings);
    }

    @PostMapping("/{meetingId}/attendance")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<AttendanceDto> markAttendance(
            @PathVariable Long meetingId,
            @RequestParam boolean present,
            @AuthenticationPrincipal MemberUser user) {
        Attendance attendance = service.markAttendance(meetingId, user, present);
        return ResponseEntity.ok(modelMapper.map(attendance, AttendanceDto.class));
    }

    @GetMapping("/{meetingId}/attendance")
    @PreAuthorize("hasRole('PRESIDENT') or hasRole('ADMIN')")
    public ResponseEntity<List<AttendanceDto>> getAttendance(@PathVariable Long meetingId) {
        List<AttendanceDto> attendanceList = service.getAttendanceForMeeting(meetingId)
                .stream()
                .map(a -> modelMapper.map(a, AttendanceDto.class))
                .toList();
        return ResponseEntity.ok(attendanceList);
    }
}
