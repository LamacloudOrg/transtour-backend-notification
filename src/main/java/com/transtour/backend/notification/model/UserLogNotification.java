package com.transtour.backend.notification.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Table(name = "user_log_notification")
@Entity
@Data
public class UserLogNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private String user;

    @Column(name="status")
    private String status;

    @Column(name="request")
    private String message;

    @Column(name="max_retry")
    private long maxRetry;

    @Column(name="created_at")
    private LocalDate createdAt;

    @Column(name="updated_at")
    private LocalTime updateAt;

}
