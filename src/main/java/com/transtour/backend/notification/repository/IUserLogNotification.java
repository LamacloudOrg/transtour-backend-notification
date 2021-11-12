package com.transtour.backend.notification.repository;

import com.transtour.backend.notification.model.Status;
import com.transtour.backend.notification.model.UserLogNotification;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Qualifier(value = "UserLogNotification")
public interface IUserLogNotification  extends JpaRepository<UserLogNotification,Long> {
    Optional<UserLogNotification> findByUser(String id);
    List<UserLogNotification> findByStatus(Status status);
}
