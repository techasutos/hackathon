package com.db.dsg.repository;

import com.db.dsg.model.SDGMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SDGMappingRepository extends JpaRepository<SDGMapping, Long> {
    List<SDGMapping> findByActionType(String actionType);

    Optional<SDGMapping> findByKeywordAndActionType(String purpose, String loanPurpose);
}
