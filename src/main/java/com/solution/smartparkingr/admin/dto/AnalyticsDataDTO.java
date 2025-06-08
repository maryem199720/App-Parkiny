package com.solution.smartparkingr.admin.dto;

import java.util.List;
import java.util.Map;

public class AnalyticsDataDTO {
    private int totalReservations;
    private double totalRevenue;
    private int activeUsers;
    private double occupancyRate;
    private int totalVehicles;
    private double dailyRevenue;
    private double averageParkingTime;
    private List<Map<String, Object>> reservationsByDay;

    // Getters et setters
    public int getTotalReservations() { return totalReservations; }
    public void setTotalReservations(int totalReservations) { this.totalReservations = totalReservations; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public int getActiveUsers() { return activeUsers; }
    public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }
    public double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(double occupancyRate) { this.occupancyRate = occupancyRate; }
    public int getTotalVehicles() { return totalVehicles; }
    public void setTotalVehicles(int totalVehicles) { this.totalVehicles = totalVehicles; }
    public double getDailyRevenue() { return dailyRevenue; }
    public void setDailyRevenue(double dailyRevenue) { this.dailyRevenue = dailyRevenue; }
    public double getAverageParkingTime() { return averageParkingTime; }
    public void setAverageParkingTime(double averageParkingTime) { this.averageParkingTime = averageParkingTime; }
    public List<Map<String, Object>> getReservationsByDay() { return reservationsByDay; }
    public void setReservationsByDay(List<Map<String, Object>> reservationsByDay) { this.reservationsByDay = reservationsByDay; }
}