package com.solution.smartparkingr.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "parking_settings")
@Data
public class ParkingSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "max_slots", nullable = false)
    private int maxSlots;

    @Column(name = "reserved_premium_slots", nullable = false)
    private int reservedPremiumSlots;

    @Embedded
    private OperatingHours operatingHours;

    @Column(name = "maintenance_mode", nullable = false)
    private boolean maintenanceMode;
}