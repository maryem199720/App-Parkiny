package com.solution.smartparkingr.admin.dto;

import lombok.Data;

import java.util.List;

@Data
public class ParkingSettingsDTO {
    private Long id;
    private int maxSlots;
    private int reservedPremiumSlots;
    private OperatingHoursDTO operatingHours;
    private boolean maintenanceMode;
    private List<SubscriptionOfferDTO> subscriptionOffers;
}