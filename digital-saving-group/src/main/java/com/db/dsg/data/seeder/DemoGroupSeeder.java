package com.db.dsg.data.seeder;


import com.db.dsg.dtos.RegisterRequest;
import com.db.dsg.model.Group;
import com.db.dsg.model.Member;
import com.db.dsg.model.MemberUser;
import com.db.dsg.repository.GroupRepository;
import com.db.dsg.service.impl.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DemoGroupSeeder {

    private final GroupRepository groupRepository;
    private final AuthService authService; // ✅ Updated to use AuthService

    public void seed() {
        if (groupRepository.existsByProjectTagIgnoreCase("sunrise-group")) {
            System.out.println("🌞 Demo group already exists. Skipping...");
            return;
        }

        System.out.println("🌱 Seeding demo group with president, treasurer, and 5 members...");

        // 1. Create group
        Group group = Group.builder()
                .name("Sunrise SHG")
                .projectTag("sunrise-group")
                .createdDate(LocalDate.now())
                .build();

        group = groupRepository.save(group);
        Long groupId = group.getId();

        // 2. Seed president
        registerUser("president1", "president123", "PRESIDENT", "Rama Devi", "9000000001", "rama@group.com", groupId);

        // 3. Seed treasurer
        registerUser("treasurer1", "treasurer123", "TREASURER", "Shanta Bai", "9000000002", "shanta@group.com", groupId);

        // 4. Seed 5 members
        List<String> memberNames = List.of("Asha", "Lakshmi", "Radha", "Meena", "Sita");

        for (int i = 0; i < memberNames.size(); i++) {
            String name = memberNames.get(i);
            registerUser(
                    "member" + (i + 1),
                    "member123",
                    "MEMBER",
                    name + " Devi",
                    "900000000" + (3 + i),
                    name.toLowerCase() + "@group.com",
                    groupId
            );
        }

        System.out.println("✅ Demo group seeded successfully.");
    }

    private void registerUser(String username, String password, String roleName,
                              String fullName, String phone, String email, Long groupId) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setRoleName(roleName);
        request.setFullName(fullName);
        request.setPhone(phone);
        request.setEmail(email);
        request.setGender(Member.Gender.FEMALE);
        request.setAadhaar("999988887777");
        request.setEkycVerified(true);
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setGroupId(groupId);

        MemberUser user = authService.register(request); // ✅ Uses AuthService
        System.out.println("👤 Registered user: " + user.getUsername() + " with role " + roleName);
    }
}