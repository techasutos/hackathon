package com.db.dsg.repository;

import com.db.dsg.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);

    List<Permission> findByNameIn(Set<String> names);

    boolean existsByName(String name);
}
