package com.transtour.backend.notification.job;

import com.transtour.backend.notification.model.Status;
import com.transtour.backend.notification.model.UserLogNotification;
import com.transtour.backend.notification.repository.IUserLogNotification;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@DisallowConcurrentExecution
public class NotificationRetry implements Job {


    @Qualifier(value = "UserLogNotification")
    @Autowired
    IUserLogNotification userLogRepo;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        List<UserLogNotification> list = userLogRepo
                .findByStatus(Status.ERROR)
                .stream()
                .filter( userLogNotification -> userLogNotification.getMaxRetry() <5)
                .peek(userLogNotification -> userLogNotification.setStatus(Status.RETRY))
                .limit(50)
                .collect(Collectors.toList());

        userLogRepo.saveAll(list);

    }
}
