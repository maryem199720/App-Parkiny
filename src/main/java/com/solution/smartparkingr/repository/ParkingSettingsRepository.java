package com.solution.smartparkingr.repository;

import com.solution.smartparkingr.model.ParkingSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSettingsRepository extends JpaRepository<ParkingSettings, Long> {
}