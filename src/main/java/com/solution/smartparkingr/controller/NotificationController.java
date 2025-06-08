package com.solution.smartparkingr.controller;

import com.solution.smartparkingr.model.Notification;
import com.solution.smartparkingr.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/notifications")
    public List<Notification> getNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        notifications.sort(Comparator.comparing(Notification::getTimestamp, Comparator.reverseOrder()));
        return notifications;
    }

    @PostMapping(value = "/notifications/{id}/mark-as-read", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + id));
        notification.setIsRead(true);
        notificationRepository.save(notification);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification marquée comme lue");
        return ResponseEntity.ok(response);
    }
}
