package com.transtour.backend.notification.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserNotificationDTO implements Serializable {

    private Long id;
    private String fcmToken;
}
