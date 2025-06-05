package com.solution.smartparkingr.load.request;

import lombok.Data;

@Data
public class UserStatsRequest {
    private int availableSpots;
    private String availableSpotsTrend;
    private int activeReservations;
    private String nextReservation;
    private String monthlySavings;
    private String savingsTrend;
    private int favoriteLocations;
    private String topLocation;
}