package com.transtour.backend.notification.model;

import javax.persistence.*;
import java.sql.Blob;
@Table(name = "USER_LOG_NOTIFICATION")
@Entity
public class UserLogNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="user_id")
    private Long user;

    @Column(name="status")
    private Status status;

    @Column(name="request")
    private Blob message;
}
