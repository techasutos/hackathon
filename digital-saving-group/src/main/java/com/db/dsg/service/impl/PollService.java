package com.db.dsg.service.impl;

import com.db.dsg.dtos.PollDto;
import com.db.dsg.dtos.PollRequestDto;
import com.db.dsg.dtos.VoteDto;
import com.db.dsg.exception.ResourceNotFoundException;
import com.db.dsg.model.*;
import com.db.dsg.repository.GroupRepository;
import com.db.dsg.repository.MemberRepository;
import com.db.dsg.repository.PollRepository;
import com.db.dsg.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepo;
    private final VoteRepository voteRepo;
    private final GroupRepository groupRepo;
    private final ModelMapper mapper;

    public PollDto createPoll(PollRequestDto dto) {
        Group group = groupRepo.findById(dto.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Poll poll = Poll.builder()
                .question(dto.getQuestion())
                .group(group)
                .deadline(dto.getDeadline())
                .closed(false)
                .build();

        Poll saved = pollRepo.save(poll);
        return toDto(saved);
    }

    public Vote vote(Long pollId, MemberUser user, VoteOption option) {
        Poll poll = pollRepo.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found"));

        if (poll.isClosed() || poll.isExpired()) {
            throw new IllegalStateException("Poll is closed or deadline passed.");
        }

        boolean alreadyVoted = voteRepo.existsByPoll_IdAndMember_Id(pollId, user.getMember().getId());
        if (alreadyVoted) {
            throw new IllegalStateException("You have already voted in this poll.");
        }

        Vote vote = Vote.builder()
                .poll(poll)
                .member(user.getMember())
                .choice(option)
                .build();

        return voteRepo.save(vote);
    }

    public Map<VoteOption, Long> tallyVotes(Long pollId) {
        Poll poll = pollRepo.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found"));

        return voteRepo.findByPoll(poll).stream()
                .collect(Collectors.groupingBy(Vote::getChoice, Collectors.counting()));
    }

    public void closePoll(Long pollId, MemberUser user) {
        Poll poll = pollRepo.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll not found"));

        if (!user.hasRole("PRESIDENT")) {
            throw new AccessDeniedException("Only PRESIDENT can close a poll.");
        }

        poll.setClosed(true);
        pollRepo.save(poll);
    }

    private PollDto toDto(Poll poll) {
        PollDto dto = mapper.map(poll, PollDto.class);
        dto.setGroupId(poll.getGroup().getId());
        dto.setGroupName(poll.getGroup().getName());
        return dto;
    }
}
