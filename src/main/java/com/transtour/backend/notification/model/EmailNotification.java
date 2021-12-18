package com.transtour.backend.notification.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "email_notification")
public class EmailNotification {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dni;

    @Column(name = "user_name")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "is_notificable")
    private boolean active;
}
