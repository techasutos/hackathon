package com.db.dsg.controller;

import com.db.dsg.model.Role;
import com.db.dsg.service.impl.RoleAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/role-assignment")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // optional: add PRESIDENT
public class RoleAssignmentController {

    private final RoleAssignmentService assignmentService;

    @PostMapping("/assign/{userId}")
    public ResponseEntity<String> assignRolesToUser(
            @PathVariable Long userId,
            @RequestBody Set<String> roleNames) {
        assignmentService.assignRoles(userId, roleNames);
        return ResponseEntity.ok("Roles assigned successfully.");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Set<Role>> getUserRoles(@PathVariable Long userId) {
        return ResponseEntity.ok(assignmentService.getUserRoles(userId));
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<String> clearUserRoles(@PathVariable Long userId) {
        assignmentService.removeAllRoles(userId);
        return ResponseEntity.ok("All roles removed.");
    }
}