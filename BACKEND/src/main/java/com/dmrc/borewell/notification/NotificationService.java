package com.dmrc.borewell.notification;

import java.util.List;

public interface NotificationService {

    Notification createNotification(String title, String message, NotificationSeverity severity, String username);

    List<Notification> getAllNotifications();

    List<Notification> getNotificationsByUser(String username);

    List<Notification> getNotificationsBySeverity(NotificationSeverity severity);

    void deleteNotification(Integer id);
}