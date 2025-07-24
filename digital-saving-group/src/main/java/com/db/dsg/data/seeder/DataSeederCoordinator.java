package com.db.dsg.data.seeder;

import com.db.dsg.model.Group;
import com.db.dsg.model.Member;
import com.db.dsg.model.MemberUser;
import com.db.dsg.repository.GroupRepository;
import com.db.dsg.repository.MemberRepository;
import com.db.dsg.repository.MemberUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeederCoordinator {

    private final PermissionSeeder permissionSeeder;
    private final RoleSeeder roleSeeder;
    private final AdminUserSeeder adminUserSeeder;
    private final DemoGroupSeeder demoGroupSeeder;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final MemberUserRepository memberUserRepository;
    private final GroupFundSeeder groupFundSeeder;
    private final LoanSeeder loanSeeder;
    private final SDGImpactSeeder sDGImpactSeeder;


    @PostConstruct
    public void runAllSeedersInOrder() {
        System.out.println("🔄 Seeding: Permissions → Roles → Admin User");

        permissionSeeder.seedPermissions();
        roleSeeder.seedRoles();
        adminUserSeeder.seedAdminUser();
        demoGroupSeeder.seed();

        System.out.println("✅ Seeding complete. Now approving and enabling members...");
        approveSeededMembers("sunrise-group");
        enableSeededUsers("sunrise-group");

        groupFundSeeder.seedFundForGroup("Sunrise SHG", new BigDecimal("200000"));
        loanSeeder.seedLoans();
        sDGImpactSeeder.seedSDGImpacts();
        System.out.println("✅ All seeders executed.");
    }

    public void approveSeededMembers(String projectTag) {
        Group group = groupRepository.findByProjectTagIgnoreCase(projectTag)
                .orElseThrow(() -> new RuntimeException("Group not found: " + projectTag));

        List<Member> members = memberRepository.findAllByGroup_Id(group.getId());

        for (Member member : members) {
            if (!member.isApproved()) {
                member.setApproved(true);

                memberRepository.save(member);
                System.out.println("✅ Approved member: " + member.getName());
            }
        }

        System.out.println("✅ All members approved for group: " + projectTag);
    }

    public void enableSeededUsers(String projectTag) {
        Group group = groupRepository.findByProjectTagIgnoreCase(projectTag)
                .orElseThrow(() -> new RuntimeException("Group not found: " + projectTag));

        List<MemberUser> users = memberUserRepository.findAllByGroup_Id(group.getId());

        for (MemberUser user : users) {
            if (!user.isEnabled()) {
                user.setEnabled(true);
                memberUserRepository.save(user);
                System.out.println("✅ Enabled user: " + user.getUsername());
            }
        }

        System.out.println("✅ All users enabled for group: " + projectTag);
    }
}
