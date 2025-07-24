package com.db.dsg.data.seeder;

import com.db.dsg.model.Group;
import com.db.dsg.model.Member;
import com.db.dsg.model.MemberUser;
import com.db.dsg.model.Role;
import com.db.dsg.repository.GroupRepository;
import com.db.dsg.repository.MemberRepository;
import com.db.dsg.repository.MemberUserRepository;
import com.db.dsg.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminUserSeeder {
    private final MemberRepository memberRepository;
    private final MemberUserRepository memberUserRepository;
    private final RoleRepository roleRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    public void seedAdminUser() {
        // 1. Check if an admin user already exists
        if (memberUserRepository.existsByRoles_Name("ADMIN")) {
            System.out.println("Admin user already exists. Skipping admin bootstrap.");
            return;
        }

        // 2. Create ADMIN role if missing
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name("ADMIN")
                        .description("Platform administrator")
                        .build()));

        // 3. Create a default Group (if none exists)
        Group defaultGroup = groupRepository.findByName("Default SHG Group")
                .orElseGet(() -> groupRepository.save(Group.builder()
                        .name("Default SHG Group")
                        .projectTag("DSG_ADMIN_BOOTSTRAP")
                        .build()));

        // 4. Create Member
        Member adminMember = Member.builder()
                .name("Admin User")
                .email("admin@example.com")
                .phone("9999999999")
                .aadhaar("111122223333")
                .ekycVerified(true)
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .gender(Member.Gender.MALE)
                .approved(true)
                .group(defaultGroup)
                .build();

        memberRepository.save(adminMember);

        // 5. Create MemberUser and link to Member
        MemberUser adminUser = MemberUser.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123")) // ✅ Change in production
                .roles(Set.of(adminRole))
                .enabled(true)
                .member(adminMember)
                .build();

        memberUserRepository.save(adminUser);

        // 6. Link back user to member (optional, since it's cascade)
        adminMember.setUser(adminUser);
        memberRepository.save(adminMember);

        System.out.println("Default ADMIN user created: username=admin, password=admin123");
    }
}