package com.db.dsg.service.impl;

import com.db.dsg.dtos.MemberDto;
import com.db.dsg.dtos.MemberRequestDto;
import com.db.dsg.exception.ResourceNotFoundException;
import com.db.dsg.model.Group;
import com.db.dsg.model.Member;
import com.db.dsg.model.MemberUser;
import com.db.dsg.repository.GroupRepository;
import com.db.dsg.repository.MemberRepository;
import com.db.dsg.repository.MemberUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberUserRepository memberUserRepository;
    private final GroupRepository groupRepository;
    private final ModelMapper modelMapper;

    // ✅ Create and link member to group and user
    @Transactional
    public MemberDto onboardMember(Long groupId, Long userId, MemberRequestDto dto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id " + groupId));

        MemberUser user = memberUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        if (memberRepository.existsByNameIgnoreCaseAndGroup_Id(dto.getName(), groupId)) {
            throw new IllegalArgumentException("Member with this name already exists in the group");
        }

        Member member = modelMapper.map(dto, Member.class);
        member.setGroup(group);
        member.setUser(user);
        member.setApproved(false);

        user.setMember(member); // bi-directional link

        Member saved = memberRepository.save(member);
        return toDto(saved);
    }

    // ✅ Get all members
    public List<MemberDto> getAllMembers() {
        return memberRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ✅ Get members by group
    public List<MemberDto> getMembersByGroup(Long groupId) {
        return memberRepository.findByGroup_Id(groupId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ✅ Get member by ID
    public Optional<MemberDto> getMemberById(Long id) {
        return memberRepository.findById(id).map(this::toDto);
    }

    // ✅ Update member
    @Transactional
    public MemberDto updateMember(Long id, MemberRequestDto dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + id));

        modelMapper.map(dto, member);
        return toDto(memberRepository.save(member));
    }

    // ✅ Delete member
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member not found with id " + id);
        }
        memberRepository.deleteById(id);
    }

    // ✅ Get current user's member profile
    public MemberDto getByUser(MemberUser user) {
        Member member = Optional.ofNullable(user.getMember())
                .orElseThrow(() -> new ResourceNotFoundException("No member profile bound to this user"));
        return toDto(member);
    }

    // ✅ Convert Member → MemberDto
    private MemberDto toDto(Member member) {
        MemberDto dto = modelMapper.map(member, MemberDto.class);
        dto.setGroupId(member.getGroup().getId());
        dto.setGroupName(member.getGroup().getName());
        dto.setRoleName(member.getDisplayRole());
        return dto;
    }
}
