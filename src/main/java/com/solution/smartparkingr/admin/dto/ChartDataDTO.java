package com.solution.smartparkingr.admin.dto;

import lombok.Data;

@Data
public class ChartDataDTO {
    private String date; // Pour dailyRevenue (format "yyyy-MM-dd")
    private String hour; // Pour occupancyTrend (format "HH:00")
    private int count; // Pour reservationsByDay
    private double revenue; // Pour dailyRevenue
    private double rate; // Pour occupancyTrend
}