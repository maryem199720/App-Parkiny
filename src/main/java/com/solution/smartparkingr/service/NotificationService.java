package com.solution.smartparkingr.service;

import com.solution.smartparkingr.model.Notification;
import java.util.Map;

public interface NotificationService {
    void checkUpcomingEvents();
    void sendNotification(Long userId, Notification.NotificationType type, String message, Map<String, Object> action);
}