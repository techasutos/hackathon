package com.db.dsg.data.seeder;

import com.db.dsg.model.Permission;
import com.db.dsg.model.Role;
import com.db.dsg.repository.PermissionRepository;
import com.db.dsg.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleSeeder {

    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;

    @PostConstruct
    public void seedRoles() {
        if (roleRepo.count() == 0) {
            // Fetch permissions by name
            Map<String, Permission> permissionMap = new HashMap<>();
            permissionRepo.findAll().forEach(p -> permissionMap.put(p.getName(), p));

            // Define role-permission mappings
            Map<String, Set<String>> rolePermissions = new HashMap<>();
            rolePermissions.put("ADMIN", new HashSet<>(permissionMap.keySet())); // All permissions

            rolePermissions.put("PRESIDENT", Set.of(
                    "loan:approve", "meeting:create", "meeting:close", "poll:close", "member:approve"
            ));

            rolePermissions.put("TREASURER", Set.of(
                    "loan:disburse", "repayment:record", "saving:view"
            ));

            rolePermissions.put("MEMBER", Set.of(
                    "loan:apply", "saving:deposit", "poll:vote", "member:view"
            ));

            for (var entry : rolePermissions.entrySet()) {
                String roleName = entry.getKey();
                Set<String> permissionNames = entry.getValue();

                Set<Permission> permissions = new HashSet<>();
                for (String name : permissionNames) {
                    Permission p = permissionMap.get(name);
                    if (p != null) permissions.add(p);
                }

                Role role = Role.builder()
                        .name(roleName)
                        .description(roleName + " role")
                        .permissions(permissions)
                        .build();

                roleRepo.save(role);
            }
        }
    }
}
