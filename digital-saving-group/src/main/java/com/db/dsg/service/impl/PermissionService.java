package com.db.dsg.service.impl;

import com.db.dsg.dtos.PermissionDTO;
import com.db.dsg.exception.ResourceNotFoundException;
import com.db.dsg.model.Permission;
import com.db.dsg.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PermissionDTO getById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
        return toDto(permission);
    }

    public PermissionDTO create(PermissionDTO dto) {
        if (permissionRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Permission with name already exists: " + dto.getName());
        }

        Permission permission = toEntity(dto);
        return toDto(permissionRepository.save(permission));
    }

    public PermissionDTO update(Long id, PermissionDTO dto) {
        Permission existing = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setCategory(dto.getCategory());

        return toDto(permissionRepository.save(existing));
    }

    public void delete(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission not found with id: " + id);
        }
        permissionRepository.deleteById(id);
    }

    // --- Mapping Helpers ---

    private PermissionDTO toDto(Permission p) {
        return PermissionDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .category(p.getCategory())
                .build();
    }

    private Permission toEntity(PermissionDTO dto) {
        return Permission.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .build();
    }
}
