package com.solution.smartparkingr.admin.dto;

import lombok.Data;

@Data
public class ReservationDTO {
    private Long id;
    private Long userId;
    private Long vehicleId;
    private Long slotId;
    private String parkingSpotName;
    private String startTime;
    private String endTime;
    private String status;
    private Double cost;
    private String createdAt;
}