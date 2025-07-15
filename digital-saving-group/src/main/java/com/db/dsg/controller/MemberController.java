package com.db.dsg.controller;

import com.db.dsg.dtos.MemberDto;
import com.db.dsg.dtos.MemberRequestDto;
import com.db.dsg.model.MemberUser;
import com.db.dsg.service.impl.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;

    // ✅ Onboard a new member to a group (bind user to group)
    @PostMapping("/group/{groupId}/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT')")
    public ResponseEntity<MemberDto> onboardMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestBody @Valid MemberRequestDto memberRequestDto) {

        MemberDto memberDto = memberService.onboardMember(groupId, userId, memberRequestDto);
        return ResponseEntity.ok(memberDto);
    }

    // ✅ Get all members
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT')")
    public List<MemberDto> getAllMembers() {
        return memberService.getAllMembers().stream()
                .map(member -> modelMapper.map(member, MemberDto.class))
                .toList();
    }

    // ✅ Get members by group
    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'TREASURER')")
    public List<MemberDto> getMembersByGroup(@PathVariable Long groupId) {
        return memberService.getMembersByGroup(groupId).stream()
                .map(member -> modelMapper.map(member, MemberDto.class))
                .toList();
    }

    // ✅ Get single member
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT', 'TREASURER')")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id)
                .map(member -> ResponseEntity.ok(modelMapper.map(member, MemberDto.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Update member info
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRESIDENT')")
    public ResponseEntity<MemberDto> updateMember(
            @PathVariable Long id,
            @RequestBody @Valid MemberRequestDto updatedDto) {

        MemberDto memberDto = memberService.updateMember(id, updatedDto);
        return ResponseEntity.ok(memberDto);
    }

    // ✅ Delete member
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Logged-in member can fetch their own profile
    @GetMapping("/me")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<MemberDto> getMyMemberInfo(@AuthenticationPrincipal MemberUser user) {
        MemberDto memberDto = memberService.getByUser(user);
        return ResponseEntity.ok(memberDto);
    }
}