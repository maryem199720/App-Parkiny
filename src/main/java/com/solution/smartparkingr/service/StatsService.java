package com.solution.smartparkingr.service;

import com.solution.smartparkingr.load.response.UserStatsResponse;

public interface StatsService {
    UserStatsResponse getUserStats(Long userId);
}