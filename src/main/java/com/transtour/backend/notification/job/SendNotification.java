package com.transtour.backend.notification.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transtour.backend.notification.dto.TravelNotificationMobileDTO;
import com.transtour.backend.notification.model.Status;
import com.transtour.backend.notification.model.UserLogNotification;
import com.transtour.backend.notification.repository.IUserLogNotification;
import com.transtour.backend.notification.service.FirebaseMessagingService;
import com.transtour.backend.notification.util.Constants;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@DisallowConcurrentExecution
public class SendNotification implements Job {


    @Qualifier(value = "UserLogNotification")
    @Autowired
    IUserLogNotification userLogRepo;

    @Autowired
    FirebaseMessagingService service;


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        List<UserLogNotification> list = userLogRepo
                .findByStatus(Status.RETRY)
                .stream()
                .peek(userLogNotification -> userLogNotification.setMaxRetry(userLogNotification.getMaxRetry()+1))
                .peek(userLogNotification -> userLogNotification.setStatus(Status.ERROR))
                .limit(50)
                .collect(Collectors.toList());
        userLogRepo.saveAll(list);


        list.
                stream().
                map(userLogNotification -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        TravelNotificationMobileDTO travelNotificationMobileDTO = mapper.readValue(userLogNotification.getMessage(), TravelNotificationMobileDTO.class);
                        return  travelNotificationMobileDTO;
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter( Objects::nonNull)
                .forEach(
                (userLogNotification -> {
                    try {
                        service.sendNotification(userLogNotification);
                        Optional<UserLogNotification>  userLogNotification1 = userLogRepo
                                .findByUser(userLogNotification.getData().get(Constants.CAR_DRIVER));
                        if (userLogNotification1.isPresent()){
                            UserLogNotification userLogNotification2 = userLogNotification1.get();
                            userLogNotification2.setStatus(Status.SENDED);
                            userLogRepo.save(userLogNotification2);
                        }
                       } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
        );


    }
}
