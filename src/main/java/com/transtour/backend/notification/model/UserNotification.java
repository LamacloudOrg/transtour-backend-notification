package com.transtour.backend.notification.model;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Blob;

@Table(name = "user_notification")
@Entity
@Data
public class UserNotification {

    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name="fcm_token")
    private String fcmToken;

}
