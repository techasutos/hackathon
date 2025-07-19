package com.db.dsg.service.impl;

import com.db.dsg.model.MemberUser;
import com.db.dsg.model.Role;
import com.db.dsg.repository.MemberUserRepository;
import com.db.dsg.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleAssignmentService {

    private final MemberUserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void assignRoles(Long userId, Set<String> roleNames) {
        MemberUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> roles = roleRepository.findAll().stream()
                .filter(role -> roleNames.contains(role.getName()))
                .collect(java.util.stream.Collectors.toSet());

        user.setRoles(roles);
        userRepository.save(user);
    }

    public Set<Role> getUserRoles(Long userId) {
        MemberUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRoles();
    }

    public void removeAllRoles(Long userId) {
        MemberUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.getRoles().clear();
        userRepository.save(user);
    }
}