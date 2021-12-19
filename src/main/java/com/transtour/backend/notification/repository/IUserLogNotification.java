package com.transtour.backend.notification.repository;

import com.transtour.backend.notification.model.UserLogNotification;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Qualifier(value = "UserLogNotification")
public interface IUserLogNotification extends JpaRepository<UserLogNotification, Long> {
    Optional<UserLogNotification> findByUser(Long id);

    List<UserLogNotification> findByStatus(String status);
}
