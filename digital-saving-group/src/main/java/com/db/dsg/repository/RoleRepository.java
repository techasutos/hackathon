package com.db.dsg.repository;

import com.db.dsg.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    Optional<Role> findByNameIgnoreCase(String roleName);
}