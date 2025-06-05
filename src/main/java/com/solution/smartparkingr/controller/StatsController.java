package com.solution.smartparkingr.controller;

import com.solution.smartparkingr.load.response.UserStatsResponse;
import com.solution.smartparkingr.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserStatsResponse> getUserStats(@PathVariable Long userId) {
        UserStatsResponse stats = statsService.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }
}