package com.dmrc.borewell.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByUsername(String username);

    List<Notification> findBySeverity(NotificationSeverity severity);

}