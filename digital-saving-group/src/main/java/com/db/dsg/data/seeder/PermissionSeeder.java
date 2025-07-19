package com.db.dsg.data.seeder;

import com.db.dsg.model.Permission;
import com.db.dsg.repository.PermissionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionSeeder {

    private final PermissionRepository permissionRepository;

    @PostConstruct
    public void seedPermissions() {
        if (permissionRepository.count() == 0) {
            List<Permission> permissions = List.of(
                    Permission.builder().name("loan:apply").description("Apply for loan").category("LOAN").build(),
                    Permission.builder().name("loan:approve").description("Approve loan").category("LOAN").build(),
                    Permission.builder().name("loan:disburse").description("Disburse loan").category("LOAN").build(),
                    Permission.builder().name("repayment:record").description("Record repayment").category("LOAN").build(),

                    Permission.builder().name("saving:deposit").description("Make saving deposit").category("SAVING").build(),
                    Permission.builder().name("saving:view").description("View savings").category("SAVING").build(),

                    Permission.builder().name("meeting:create").description("Create group meeting").category("GOVERNANCE").build(),
                    Permission.builder().name("meeting:close").description("Close group meeting").category("GOVERNANCE").build(),

                    Permission.builder().name("poll:vote").description("Vote in poll").category("GOVERNANCE").build(),
                    Permission.builder().name("poll:close").description("Close poll").category("GOVERNANCE").build(),

                    Permission.builder().name("member:approve").description("Approve group member").category("MEMBER").build(),
                    Permission.builder().name("member:view").description("View member profile").category("MEMBER").build()
            );

            permissionRepository.saveAll(permissions);
        }
    }
}
