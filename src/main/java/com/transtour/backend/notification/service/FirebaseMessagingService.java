package com.transtour.backend.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.dozermapper.core.Mapper;
import com.transtour.backend.notification.controller.NotificationController;
import com.transtour.backend.notification.dto.ResultDTO;
import com.transtour.backend.notification.dto.TravelInfoNotificationMobileDTO;
import com.transtour.backend.notification.dto.TravelNotificationMobileDTO;
import com.transtour.backend.notification.exception.NotificationMobileError;
import com.transtour.backend.notification.exception.UserNotExist;
import com.transtour.backend.notification.model.Status;
import com.transtour.backend.notification.model.UserLogNotification;
import com.transtour.backend.notification.model.UserNotification;
import com.transtour.backend.notification.repository.IUserLogNotification;
import com.transtour.backend.notification.repository.IUserNotifiactionRepository;
import com.transtour.backend.notification.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

@Service
public class FirebaseMessagingService {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private static final String SEND_NOTIFICATION_TO_MOBILE = "https://fcm.googleapis.com/fcm/send";

    private RestTemplate restTemplate;

    @Qualifier(value = "UserNotification")
    @Autowired
    IUserNotifiactionRepository userNotiRepo;

    @Qualifier(value = "UserLogNotification")
    @Autowired
    IUserLogNotification userLogRepo;


    @Autowired
    private Mapper mapper;


    @Value("${token-header}")
    String token;

    @Autowired
    public FirebaseMessagingService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ResponseEntity sendNotification(TravelNotificationMobileDTO travelNotificationMobileDTO) throws IOException {
        TravelNotificationMobileDTO travelNotificationMobileDTO1 = setToken(travelNotificationMobileDTO);
        callSendNotificationToMobile(travelNotificationMobileDTO1);
        TravelInfoNotificationMobileDTO travelNotificationMobileDTO2 = new TravelInfoNotificationMobileDTO();
        mapper.map(travelNotificationMobileDTO1, travelNotificationMobileDTO2);
        return callSendNotificationToMobile(travelNotificationMobileDTO2);
    }

    @Async
    public void callSendNotificationToMobile(TravelNotificationMobileDTO travelNotificationMobileDTO) throws IOException {

        HttpEntity<TravelNotificationMobileDTO> entity = new HttpEntity<>(travelNotificationMobileDTO, getHttpHeaders());
        restTemplate.postForEntity(SEND_NOTIFICATION_TO_MOBILE, entity, ResultDTO.class);

    }

    public ResponseEntity callSendNotificationToMobile(TravelInfoNotificationMobileDTO travelInfoNotificationMobileDTO) throws IOException {

        HttpEntity<TravelInfoNotificationMobileDTO> entity = new HttpEntity<>(travelInfoNotificationMobileDTO, getHttpHeaders());
        ResponseEntity<ResultDTO> result = restTemplate.postForEntity(SEND_NOTIFICATION_TO_MOBILE, entity, ResultDTO.class);
        UserLogNotification logNotification = new UserLogNotification();

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(travelInfoNotificationMobileDTO);

        logNotification.setMessage(json);
        logNotification.setUser((String) travelInfoNotificationMobileDTO.getData().get(Constants.CAR_DRIVER));
        logNotification.setStatus(Status.SENDED.toString());
        logNotification.setCreatedAt(LocalDate.now());
        logNotification.setUpdateAt(LocalTime.now());

        if (!result.getStatusCode().is2xxSuccessful() || result.getBody().getSuccess() != 1) {
            logNotification.setStatus(Status.ERROR.toString());
        }
        userLogRepo.save(logNotification);

        if (logNotification.getStatus().equals(Status.ERROR)) {
            throw new NotificationMobileError("no se pudo notificar al chofer" + logNotification.getUser());
        }

        return ResponseEntity.ok("Se envio notificacion con la info");
    }

    private TravelNotificationMobileDTO setToken(TravelNotificationMobileDTO travelNotificationMobileDTO) {
        Optional.ofNullable(travelNotificationMobileDTO.getData().getOrDefault(Constants.CAR_DRIVER, "2")).orElse("2");
        String carDriver = (String) travelNotificationMobileDTO.getData().get(Constants.CAR_DRIVER);

        log.debug("carDriver", carDriver);
        Optional<UserNotification> userNotification = userNotiRepo.findById(Long.parseLong(carDriver));
        if (!userNotification.isPresent()) throw new UserNotExist("el chofer no se encuentra registrado" + carDriver);

        travelNotificationMobileDTO.setTo(userNotification.get().getFcmToken());
        travelNotificationMobileDTO.getData().remove("car-driver");

        return travelNotificationMobileDTO;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.AUTHORIZATION, token);
        return headers;
    }

}