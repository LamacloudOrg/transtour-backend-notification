package com.transtour.backend.notification.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "email_notification")
public class EmailNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_name")
    private String username;

    @Column(name="email")
    private String email;

    @Column(name="is_notificable")
    private boolean active;
}
