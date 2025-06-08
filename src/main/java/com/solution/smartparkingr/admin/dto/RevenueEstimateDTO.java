package com.solution.smartparkingr.admin.dto;

public class RevenueEstimateDTO {
    private double monthly;
    private double annual;

    // Getters et Setters
    public double getMonthly() {
        return monthly;
    }

    public void setMonthly(double monthly) {
        this.monthly = monthly;
    }

    public double getAnnual() {
        return annual;
    }

    public void setAnnual(double annual) {
        this.annual = annual;
    }
}