package com.db.dsg.controller;

import com.db.dsg.model.SDGGoal;
import com.db.dsg.model.SDGImpact;
import com.db.dsg.service.SDGImpactServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sdg-impact")
@RequiredArgsConstructor
public class SDGImpactController {

    private final SDGImpactServiceI impactService;

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<SDGImpact>> getByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(impactService.getImpactsByGroup(groupId));
    }

    @GetMapping("/group/{groupId}/month/{month}")
    public ResponseEntity<List<SDGImpact>> getByGroupAndMonth(@PathVariable Long groupId, @PathVariable String month) {
        return ResponseEntity.ok(impactService.getImpactsByGroupAndMonth(groupId, month));
    }

    @GetMapping("/group/{groupId}/goal/{goal}")
    public ResponseEntity<List<SDGImpact>> getByGroupAndGoal(@PathVariable Long groupId, @PathVariable SDGGoal goal) {
        return ResponseEntity.ok(impactService.getImpactsByGroupAndGoal(groupId, goal));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<List<SDGImpact>> getImpacts(@PathVariable Long groupId) {
        return ResponseEntity.ok(impactService.getImpactsByGroup(groupId));
    }

    @GetMapping("/{groupId}/summary")
    public ResponseEntity<List<Map<String, Object>>> getImpactSummary(@PathVariable Long groupId) {
        return ResponseEntity.ok(impactService.getSummaryByGroup(groupId));
    }
}