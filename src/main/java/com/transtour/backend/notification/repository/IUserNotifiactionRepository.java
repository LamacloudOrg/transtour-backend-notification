package com.transtour.backend.notification.repository;

import com.transtour.backend.notification.model.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IUserNotifiactionRepository extends JpaRepository<UserNotification,Long> {
    Optional<UserNotification> findById(Long id);
}
