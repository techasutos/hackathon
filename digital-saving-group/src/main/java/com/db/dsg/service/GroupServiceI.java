package com.db.dsg.service;

import com.db.dsg.dtos.GroupDTO;

import java.util.List;
import java.util.Optional;

public interface GroupServiceI {
    GroupDTO createGroup(GroupDTO dto);

    GroupDTO updateGroup(Long id, GroupDTO dto);

    void deleteGroup(Long id);

    List<GroupDTO> listGroups();

    Optional<GroupDTO> getGroupById(Long id);
}
