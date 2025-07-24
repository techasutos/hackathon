package com.db.dsg.repository;

import com.db.dsg.model.Group;
import com.db.dsg.model.SDGGoal;
import com.db.dsg.model.SDGImpact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SDGImpactRepository extends JpaRepository<SDGImpact, Long> {
    List<SDGImpact> findByGroupId(Long groupId);

    List<SDGImpact> findByGroupIdAndGoal(Long groupId, SDGGoal goal);

    List<SDGImpact> findByGroupIdAndPeriodStartingWith(Long groupId, String year);

    List<SDGImpact> findByGroupIdAndPeriod(Long groupId, String period);

    List<SDGImpact> findByGroup(Group group);

    @Query("SELECT i.goal, COUNT(i) FROM SDGImpact i WHERE i.group = :group GROUP BY i.goal")
    List<Object[]> countByGoal(Group group);
}