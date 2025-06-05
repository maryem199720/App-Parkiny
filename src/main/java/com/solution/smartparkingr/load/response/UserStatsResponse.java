package com.solution.smartparkingr.load.response;

import lombok.Data;

@Data
public class UserStatsResponse {
    private int availableSpots;
    private String availableSpotsTrend;
    private int activeReservations;
    private String nextReservation;
    private String monthlySavings;
    private String savingsStatus;
    private int reservationsThisMonth;
    private String totalReservations;
}