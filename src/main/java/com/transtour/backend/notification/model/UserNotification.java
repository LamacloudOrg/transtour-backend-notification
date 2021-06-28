package com.transtour.backend.notification.model;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class UserNotification {

    @Id
    private Long id;

    @Column(name="fcm_token")
    private String fcmToken;

    @Column(name="status")
    private String status;

    @Column(name="message")
    private String message;
}
