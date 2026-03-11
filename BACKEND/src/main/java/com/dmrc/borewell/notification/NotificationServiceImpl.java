package com.dmrc.borewell.notification;

import com.dmrc.borewell.config.websocket.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // ✅ Real-time broadcasting
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public Notification createNotification(String title, String message,
                                           NotificationSeverity severity,
                                           String username) {

        Notification notification = new Notification(title, message, severity, username);

        // 🔹 Existing DB logic (UNCHANGED)
        Notification saved = notificationRepository.save(notification);

        // 🔹 Convert to WebSocket DTO (NEW but does not change logic)
        NotificationMessage messageDto = new NotificationMessage(
                saved.getId(),
                saved.getTitle(),
                saved.getMessage(),
                saved.getSeverity(),
                saved.getUsername()
        );

        // 🔹 Real-time broadcast to ALL logged-in users
        messagingTemplate.convertAndSend(
                "/topic/notifications",
                messageDto
        );

        return saved;
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getNotificationsByUser(String username) {
        return notificationRepository.findByUsername(username);
    }

    @Override
    public List<Notification> getNotificationsBySeverity(NotificationSeverity severity) {
        return notificationRepository.findBySeverity(severity);
    }

    @Override
    public void deleteNotification(Integer id) {
        notificationRepository.deleteById(id);
    }
}