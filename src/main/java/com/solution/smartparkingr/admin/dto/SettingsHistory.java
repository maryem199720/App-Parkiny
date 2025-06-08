package com.solution.smartparkingr.admin.dto;

import com.solution.smartparkingr.model.ParkingSettings;
import lombok.Data;

@Data
public class SettingsHistory {
    private String timestamp;
    private ParkingSettings changes;
}