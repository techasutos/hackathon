package com.db.dsg.mapper;

import com.db.dsg.dtos.RoleDTO;
import com.db.dsg.model.Permission;
import com.db.dsg.model.Role;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleDTO toDto(Role role) {
        Set<String> permissionNames = role.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        return new RoleDTO(role.getId(), role.getName(), role.getDescription(), permissionNames);
    }

    public Role toEntity(RoleDTO dto, Set<Permission> permissions) {
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setPermissions(permissions);
        return role;
    }
}
