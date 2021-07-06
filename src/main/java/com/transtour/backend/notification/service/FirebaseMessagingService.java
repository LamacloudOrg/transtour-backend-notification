package com.transtour.backend.notification.service;

import com.transtour.backend.notification.controller.NotificationController;
import com.transtour.backend.notification.dto.TravelNotificationMobileDTO;
import com.transtour.backend.notification.dto.ResultDTO;
import com.transtour.backend.notification.exception.NotificationMobileError;
import com.transtour.backend.notification.model.UserNotification;
import com.transtour.backend.notification.repository.IUserNotifiactionRepository;
import com.transtour.backend.notification.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${token-header}")
    String token;

    @Autowired
    public FirebaseMessagingService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ResponseEntity sendNotification(TravelNotificationMobileDTO travelNotificationMobileDTO) throws IOException {
        ResponseEntity response = callSendNotificationToMobile(travelNotificationMobileDTO);
        return response;
    }

    public ResponseEntity callSendNotificationToMobile (TravelNotificationMobileDTO travelNotificationMobileDTO) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.AUTHORIZATION, token);

        Optional.ofNullable(travelNotificationMobileDTO.getData().getOrDefault(Constants.CAR_DRIVER,"2")).orElse("2");
        String carDriver = travelNotificationMobileDTO.getData().get(Constants.CAR_DRIVER);

        log.debug("carDriver",carDriver);
        Optional<UserNotification> userNotification = userNotiRepo.findById(Long.parseLong(carDriver));
        travelNotificationMobileDTO.setTo(userNotification.get().getFcmToken());
        travelNotificationMobileDTO.getData().remove("car-driver");

        HttpEntity<TravelNotificationMobileDTO> entity = new HttpEntity<>(travelNotificationMobileDTO, headers);
        ResponseEntity<ResultDTO> result = restTemplate.postForEntity(SEND_NOTIFICATION_TO_MOBILE, entity, ResultDTO.class);

        if(!result.getStatusCode().is2xxSuccessful() || result.getBody().getSuccess() !=1) {
            //TODO implementar un logeo en db con el mensaje de error
            throw new NotificationMobileError("no se notifico");
        }

        log.info("Se notifico al chofer");

        return  ResponseEntity.ok("Se notifico al chofer");
    }

}