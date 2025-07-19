package com.db.dsg.repository;

import com.db.dsg.model.Member;
import com.db.dsg.model.Poll;
import com.db.dsg.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByPollIdAndMemberId(Long pollId, Long memberId);

    List<Vote> findByPollId(Long pollId);

    Optional<Vote> findByPollAndMember(Poll poll, Member member);
    List<Vote> findByPoll(Poll poll);

    // ✅ Checks if a vote already exists for a poll by a member
    boolean existsByPoll_IdAndMember_Id(Long pollId, Long memberId);
}
