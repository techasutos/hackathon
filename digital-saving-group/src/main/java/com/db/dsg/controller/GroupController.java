package com.db.dsg.controller;

import com.db.dsg.dtos.GroupDTO;
import com.db.dsg.service.impl.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GroupDTO> createGroup(@RequestBody @Valid GroupDTO groupDTO) {
        return ResponseEntity.ok(groupService.createGroup(groupDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GroupDTO> updateGroup(@PathVariable Long id, @RequestBody @Valid GroupDTO dto) {
        return ResponseEntity.ok(groupService.updateGroup(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRESIDENT','TREASURER')")
    public ResponseEntity<List<GroupDTO>> listGroups() {
        return ResponseEntity.ok(groupService.listGroups());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PRESIDENT','TREASURER')")
    public ResponseEntity<GroupDTO> getGroupById(@PathVariable Long id) {
        return groupService.getGroupById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}