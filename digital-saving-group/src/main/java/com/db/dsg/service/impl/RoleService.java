package com.db.dsg.service.impl;

import com.db.dsg.dtos.CreateRoleRequest;
import com.db.dsg.dtos.RoleDTO;
import com.db.dsg.model.Permission;
import com.db.dsg.model.Role;
import com.db.dsg.repository.PermissionRepository;
import com.db.dsg.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return toDTO(role);
    }

    public RoleDTO createRole(CreateRoleRequest request) {
        Set<Permission> permissions = permissionRepository.findAll().stream()
                .filter(p -> request.getPermissionNames().contains(p.getName()))
                .collect(Collectors.toSet());

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(permissions)
                .build();

        return toDTO(roleRepository.save(role));
    }

    public RoleDTO updateRole(Long id, CreateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        Set<Permission> permissions = permissionRepository.findAll().stream()
                .filter(p -> request.getPermissionNames().contains(p.getName()))
                .collect(Collectors.toSet());

        role.setPermissions(permissions);
        return toDTO(roleRepository.save(role));
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    private RoleDTO toDTO(Role role) {
        return new RoleDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet())
        );
    }
}
