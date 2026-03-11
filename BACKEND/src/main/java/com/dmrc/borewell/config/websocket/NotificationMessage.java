package com.dmrc.borewell.config.websocket;

import com.dmrc.borewell.notification.NotificationSeverity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

    private Integer id;
    private String title;
    private String message;
    private NotificationSeverity severity;
    private String username;
}