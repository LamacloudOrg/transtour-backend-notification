package com.transtour.backend.notification.service;

import com.transtour.backend.notification.controller.NotificationController;
import com.transtour.backend.notification.dto.TravelNotificationMobileDTO;
import com.transtour.backend.notification.dto.ResultDTO;
import com.transtour.backend.notification.exception.NotificationMobileError;
import com.transtour.backend.notification.model.UserNotification;
import com.transtour.backend.notification.repository.IUserNotifiactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Service
public class FirebaseMessagingService {

    private static Logger log = LoggerFactory.getLogger(NotificationController.class);
    private static final String SEND_NOTIFICATION_TO_MOBILE = "https://fcm.googleapis.com/fcm/send";

    private RestTemplate restTemplate;

    @Qualifier(value = "UserNotification")
    @Autowired
    IUserNotifiactionRepository userNotiRepo;

    @Autowired
    public FirebaseMessagingService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ResponseEntity sendNotification(TravelNotificationMobileDTO travelNotificationMobileDTO, String token) throws IOException {
        ResponseEntity response = callSendNotificationToMobile(travelNotificationMobileDTO, token);
        return response;
    }

    public ResponseEntity callSendNotificationToMobile (TravelNotificationMobileDTO travelNotificationMobileDTO, String token) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.AUTHORIZATION, System.getenv("token-header"));

        String carDriver = travelNotificationMobileDTO.getData().get("car-driver");
        Optional<UserNotification> userNotification = userNotiRepo.findById(Long.parseLong(carDriver));

        travelNotificationMobileDTO.setTo(userNotification.get().getFcmToken());
        travelNotificationMobileDTO.getData().remove("car-driver");

        HttpEntity<TravelNotificationMobileDTO> entity = new HttpEntity<>(travelNotificationMobileDTO, headers);
        ResponseEntity<ResultDTO> result = restTemplate.postForEntity(SEND_NOTIFICATION_TO_MOBILE, entity, ResultDTO.class);

        if(!result.getStatusCode().is2xxSuccessful() || result.getBody().getSuccess() !=1) {
            //TODO implementar un logeo en db con el mensaje de error
            throw new NotificationMobileError("no se notifico");
        }

        return  ResponseEntity.ok("Se notifico al chofer");
    }

}