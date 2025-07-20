package com.db.dsg.service.impl;

import com.db.dsg.dtos.AuthRequest;
import com.db.dsg.dtos.RegisterRequest;
import com.db.dsg.model.Group;
import com.db.dsg.model.Member;
import com.db.dsg.model.MemberUser;
import com.db.dsg.model.Role;
import com.db.dsg.repository.GroupRepository;
import com.db.dsg.repository.MemberRepository;
import com.db.dsg.repository.MemberUserRepository;
import com.db.dsg.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepo;
    private final MemberUserRepository userRepo;
    private final GroupRepository groupRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public MemberUser register(RegisterRequest request) {
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Group group = groupRepo.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        boolean exists = memberRepo.existsByNameIgnoreCaseAndGroup_Id(request.getFullName(), request.getGroupId());
        if (exists) {
            throw new RuntimeException("Member with this name already exists in this group");
        }

        Member member = Member.builder()
                .name(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .aadhaar(request.getAadhaar())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .ekycVerified(request.isEkycVerified())
                .group(group)
                .approved(false)
                .build();

        member = memberRepo.save(member);

        // ✅ Resolve Role by name
        Role role = roleRepo.findByNameIgnoreCase(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Invalid role: " + request.getRoleName()));

        MemberUser user = MemberUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .group(group)
                .member(member)
                .roles(Set.of(role))
                .build();

        userRepo.save(user);
        return user;
    }

    public MemberUser authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        return userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String generateToken(MemberUser user) {
        return jwtService.generateToken(user);
    }
}
