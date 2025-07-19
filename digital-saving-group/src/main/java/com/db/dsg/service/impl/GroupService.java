package com.db.dsg.service.impl;

import com.db.dsg.dtos.GroupDTO;
import com.db.dsg.exception.ResourceNotFoundException;
import com.db.dsg.model.Group;
import com.db.dsg.model.Member;
import com.db.dsg.repository.GroupRepository;
import com.db.dsg.repository.MemberRepository;
import com.db.dsg.service.GroupServiceI;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService implements GroupServiceI {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    @Override
    public GroupDTO createGroup(GroupDTO dto) {
        if (groupRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Group with name already exists.");
        }

        Group group = new Group();
        group.setName(dto.getName());
        group.setProjectTag(dto.getProjectTag());
        group.setCreatedDate(dto.getCreatedDate() != null ? dto.getCreatedDate() : LocalDate.now());

        // Optionally add members if provided
        if (dto.getMemberIds() != null) {
            List<Member> members = memberRepository.findAllById(dto.getMemberIds());
            members.forEach(m -> m.setGroup(group));
            group.setMembers(members);
        }

        return modelMapper.map(groupRepository.save(group), GroupDTO.class);
    }

    @Override
    public GroupDTO updateGroup(Long id, GroupDTO dto) {
        Group existing = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        existing.setName(dto.getName());
        existing.setProjectTag(dto.getProjectTag());

        return modelMapper.map(groupRepository.save(existing), GroupDTO.class);
    }

    @Override
    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    @Override
    public List<GroupDTO> listGroups() {
        return groupRepository.findAll().stream()
                .map(group -> {
                    GroupDTO dto = modelMapper.map(group, GroupDTO.class);
                    dto.setMemberIds(group.getMembers().stream().map(Member::getId).toList());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GroupDTO> getGroupById(Long id) {
        return groupRepository.findById(id)
                .map(group -> {
                    GroupDTO dto = modelMapper.map(group, GroupDTO.class);
                    dto.setMemberIds(group.getMembers().stream().map(Member::getId).toList());
                    return dto;
                });
    }
}