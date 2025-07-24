package com.db.dsg.service;

import com.db.dsg.model.SDGGoal;
import com.db.dsg.model.SDGImpact;

import java.util.List;
import java.util.Map;

public interface SDGImpactServiceI {
    List<SDGImpact> getImpactsByGroup(Long groupId);
    List<SDGImpact> getImpactsByGroupAndMonth(Long groupId, String month);
    List<SDGImpact> getImpactsByGroupAndGoal(Long groupId, SDGGoal goal);
    List<Map<String, Object>> getSummaryByGroup(Long groupId);
}
