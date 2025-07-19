package com.db.dsg.data.seeder;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeederCoordinator {

    private final PermissionSeeder permissionSeeder;
    private final RoleSeeder roleSeeder;
    private final AdminUserSeeder adminUserSeeder;

    @PostConstruct
    public void runAllSeedersInOrder() {
        System.out.println("🔄 Seeding: Permissions → Roles → Admin User");

        permissionSeeder.seedPermissions();
        roleSeeder.seedRoles();
        adminUserSeeder.seedAdminUser();

        System.out.println("✅ All seeders executed.");
    }
}
