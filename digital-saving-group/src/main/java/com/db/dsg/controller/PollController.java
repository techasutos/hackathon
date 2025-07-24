package com.db.dsg.controller;

import com.db.dsg.dtos.PollDto;
import com.db.dsg.dtos.PollRequestDto;
import com.db.dsg.model.MemberUser;
import com.db.dsg.model.Vote;
import com.db.dsg.model.VoteOption;
import com.db.dsg.service.impl.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
public class PollController {
    private final PollService service;

    @PostMapping
    @PreAuthorize("hasRole('PRESIDENT') or hasRole('ADMIN')")
    public ResponseEntity<PollDto> createPoll(@RequestBody @Valid PollRequestDto pollRequestDto) {
        return ResponseEntity.ok(service.createPoll(pollRequestDto));
    }

    @PostMapping("/{pollId}/vote")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Vote> vote(
            @PathVariable Long pollId,
            @RequestParam VoteOption choice,
            @AuthenticationPrincipal MemberUser user) {
        return ResponseEntity.ok(service.vote(pollId, user, choice));
    }

    @GetMapping("/{pollId}/tally")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRESIDENT')")
    public ResponseEntity<Map<VoteOption, Long>> tallyVotes(@PathVariable Long pollId) {
        return ResponseEntity.ok(service.tallyVotes(pollId));
    }

    @PostMapping("/{pollId}/close")
    @PreAuthorize("hasRole('PRESIDENT')")
    public ResponseEntity<Void> closePoll(
            @PathVariable Long pollId,
            @AuthenticationPrincipal MemberUser user) {
        service.closePoll(pollId, user);
        return ResponseEntity.ok().build();
    }
}
