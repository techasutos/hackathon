package com.db.dsg.service.impl;

import com.db.dsg.model.Group;
import com.db.dsg.model.SDGGoal;
import com.db.dsg.model.SDGImpact;
import com.db.dsg.repository.GroupRepository;
import com.db.dsg.repository.SDGImpactRepository;
import com.db.dsg.service.SDGImpactServiceI;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SDGImpactService implements SDGImpactServiceI {

    private final SDGImpactRepository impactRepo;

    @Override
    public List<SDGImpact> getImpactsByGroup(Long groupId) {
        return impactRepo.findByGroupId(groupId);
    }

    @Override
    public List<SDGImpact> getImpactsByGroupAndMonth(Long groupId, String month) {
        return impactRepo.findByGroupIdAndPeriod(groupId, month);
    }

    @Override
    public List<SDGImpact> getImpactsByGroupAndGoal(Long groupId, SDGGoal goal) {
        return impactRepo.findByGroupIdAndGoal(groupId, goal);
    }

    @Override
    public List<Map<String, Object>> getSummaryByGroup(Long groupId) {
        List<SDGImpact> impacts = impactRepo.findByGroupId(groupId);

        return impacts.stream()
                .collect(Collectors.groupingBy(SDGImpact::getGoal))
                .entrySet()
                .stream()
                .map(entry -> {
                    SDGGoal goal = entry.getKey();
                    List<SDGImpact> goalImpacts = entry.getValue();

                    int totalJobs = goalImpacts.stream()
                            .mapToInt(i -> Optional.ofNullable(i.getJobsCreated()).orElse(0))
                            .sum();

                    int totalWomen = goalImpacts.stream()
                            .mapToInt(i -> Optional.ofNullable(i.getWomenEmpowered()).orElse(0))
                            .sum();

                    BigDecimal totalSavings = goalImpacts.stream()
                            .map(i -> Optional.ofNullable(i.getSavingsGrowth()).orElse(BigDecimal.ZERO))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    Map<String, Object> map = new HashMap<>();
                    map.put("goalName", goal.name());
                    map.put("totalActions", goalImpacts.size());
                    map.put("totalJobs", totalJobs);
                    map.put("totalWomen", totalWomen);
                    map.put("totalSavings", totalSavings);
                    return map;
                })
                .collect(Collectors.toList());
    }
}
