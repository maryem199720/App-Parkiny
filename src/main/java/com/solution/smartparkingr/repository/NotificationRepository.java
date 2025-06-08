package com.solution.smartparkingr.repository;

import com.solution.smartparkingr.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Removed invalid method: findByUserIdOrderByCreatedAtDesc
}