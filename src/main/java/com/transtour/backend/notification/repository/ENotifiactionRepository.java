package com.transtour.backend.notification.repository;

import com.transtour.backend.notification.model.EmailNotification;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Qualifier("EmailNotification")
@Repository
public interface ENotifiactionRepository extends JpaRepository<EmailNotification, Integer> {
    List<EmailNotification> findByActive(boolean isActive);

    EmailNotification findByDni(Long dni);
}
