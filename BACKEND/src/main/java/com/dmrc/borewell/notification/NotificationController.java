package com.dmrc.borewell.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification saved = notificationService.createNotification(
                notification.getTitle(),
                notification.getMessage(),
                notification.getSeverity(),
                notification.getUsername()
        );
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/user/{username}")
    public List<Notification> getNotificationsByUser(@PathVariable String username) {
        return notificationService.getNotificationsByUser(username);
    }

    @GetMapping("/severity/{severity}")
    public List<Notification> getNotificationsBySeverity(@PathVariable NotificationSeverity severity) {
        return notificationService.getNotificationsBySeverity(severity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}