package com.transtour.backend.notification.configuration;
import com.transtour.backend.notification.model.EmailNotification;
import com.transtour.backend.notification.repository.ENotifiactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {

    @Qualifier("EmailNotification")
    @Autowired
    ENotifiactionRepository eRpo;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<EmailNotification> notifications = new ArrayList<>();

        EmailNotification kike = new EmailNotification(null,"kike","email@gmail.com",false);
        EmailNotification pali = new EmailNotification(null,"pali","pomalianni@gmail.com",true);
        EmailNotification charly = new EmailNotification(null,"charly","cnlafitte@gmail.com",true);
        notifications.add(kike);
        notifications.add(pali);
        notifications.add(charly);
        eRpo.saveAll(notifications);
    }
}
