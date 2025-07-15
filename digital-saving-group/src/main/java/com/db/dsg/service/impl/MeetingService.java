package com.db.dsg.service.impl;

import com.db.dsg.dtos.CreateMeetingRequest;
import com.db.dsg.exception.ResourceNotFoundException;
import com.db.dsg.model.*;
import com.db.dsg.repository.AttendanceRepository;
import com.db.dsg.repository.GroupRepository;
import com.db.dsg.repository.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final GroupRepository groupRepository;
    private final AttendanceRepository attendanceRepository;
    private final ModelMapper modelMapper;

    // ✅ Create meeting
    public Meeting createMeeting(CreateMeetingRequest request) {
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Meeting meeting = new Meeting();
        meeting.setGroup(group);
        meeting.setScheduledAt(request.getDate());
        meeting.setAgenda(request.getAgenda());

        return meetingRepository.save(meeting);
    }

    // ✅ Get meetings by group
    public List<Meeting> getMeetingsByGroup(Long groupId) {
        return meetingRepository.findByGroupIdOrderByScheduledAtDesc(groupId);
    }

    // ✅ Mark attendance
    public Attendance markAttendance(Long meetingId, MemberUser user, boolean present) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found"));

        Member member = user.getMember();

        Optional<Attendance> existing = attendanceRepository.findByMeetingAndMember(meeting, member);
        if (existing.isPresent()) {
            Attendance attendance = existing.get();
            attendance.setPresent(present);
            return attendanceRepository.save(attendance);
        }

        Attendance newAttendance = new Attendance();
        newAttendance.setMeeting(meeting);
        newAttendance.setMember(member);
        newAttendance.setPresent(present);
        return attendanceRepository.save(newAttendance);
    }

    // ✅ Get attendance for a meeting
    public List<Attendance> getAttendanceForMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found"));
        return attendanceRepository.findByMeeting(meeting);
    }
}
