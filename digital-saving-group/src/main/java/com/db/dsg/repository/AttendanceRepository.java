package com.db.dsg.repository;

import com.db.dsg.model.Attendance;
import com.db.dsg.model.Meeting;
import com.db.dsg.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByMeetingAndMember(Meeting meeting, Member member);

    List<Attendance> findByMeeting(Meeting meeting);
}
