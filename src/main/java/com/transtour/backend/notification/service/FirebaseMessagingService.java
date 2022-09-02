package com.transtour.backend.notification.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.dozermapper.core.Mapper;
import com.transtour.backend.notification.controller.NotificationController;
import com.transtour.backend.notification.dto.ResultDTO;
import com.transtour.backend.notification.dto.TravelInfoNotificationMobileDTO;
import com.transtour.backend.notification.dto.TravelNotificationMobileDTO;
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
import java.util.Collections;
import java.util.Optional;

@Service
public class FirebaseMessagingService {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private static final String SEND_NOTIFICATION_TO_MOBILE = "https://fcm.googleapis.com/fcm/send";

    @Value("${token-header-android}")
    String TOKEN_ANDROID;
    @Value("${token-header-ios}")
    String TOKEN_IOS;

    private RestTemplate restTemplate;

    @Qualifier(value = "UserNotification")
    @Autowired
    IUserNotifiactionRepository userNotiRepo;

    @Qualifier(value = "UserLogNotification")
    @Autowired
    IUserLogNotification userLogRepo;

    @Autowired
    private Mapper mapper;

    @Autowired
    public FirebaseMessagingService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ResponseEntity<?> sendNotification(TravelNotificationMobileDTO travelNotificationMobileDTO) throws IOException {
        log.info("notificando viaje " + travelNotificationMobileDTO.getData().get(Constants.CAR_DRIVER).toString());
        Optional<UserNotification> userNotification = userNotiRepo.findById(Long.valueOf(String.valueOf(travelNotificationMobileDTO.getData().get(Constants.CAR_DRIVER))));
        if (userNotification.isPresent()) {
            return ResponseEntity.badRequest().body("Driver not found");
        }

        //send notification
        String device = userNotification.get().getDevice(); //IOS or ANDROID
        travelNotificationMobileDTO.setTo(userNotification.get().getFcmToken()); //seteo token firebase
        //travelNotificationMobileDTO.getData().remove("car-driver");


        // Si es IOS se envia 2 veces x issue en firebase.
        if (device.equals("IOS")) {
            callSendNotificationToMobile(travelNotificationMobileDTO, device);
        }

        TravelInfoNotificationMobileDTO travelNotificationMobileDTO2 = new TravelInfoNotificationMobileDTO();
        mapper.map(travelNotificationMobileDTO, travelNotificationMobileDTO2);
        return callSendNotificationToMobile(travelNotificationMobileDTO2, device);
    }

    @Async
    public void callSendNotificationToMobile(TravelNotificationMobileDTO travelNotificationMobileDTO, String device) throws IOException {
        try {
            HttpEntity<TravelNotificationMobileDTO> entity = new HttpEntity<>(travelNotificationMobileDTO, getHttpHeaders(device));
            ResponseEntity<ResultDTO> result = restTemplate.postForEntity(SEND_NOTIFICATION_TO_MOBILE, entity, ResultDTO.class);

            if (!result.getStatusCode().is2xxSuccessful() || result.getBody().getSuccess() != 1) {
                log.info("No se pudo enviar la notificacion" + Status.ERROR);
            } else {
                log.info("Se envio correctamente");
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public ResponseEntity callSendNotificationToMobile(TravelInfoNotificationMobileDTO travelInfoNotificationMobileDTO, String device) throws IOException {

        try {
            HttpEntity<TravelInfoNotificationMobileDTO> entity = new HttpEntity<>(travelInfoNotificationMobileDTO, getHttpHeaders(device));
            ResponseEntity<ResultDTO> result = restTemplate.postForEntity(SEND_NOTIFICATION_TO_MOBILE, entity, ResultDTO.class);
            UserLogNotification logNotification = new UserLogNotification();

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(travelInfoNotificationMobileDTO);

            logNotification.setMessage(json);
            logNotification.setUser(String.valueOf(travelInfoNotificationMobileDTO.getData().get(Constants.CAR_DRIVER)));
            logNotification.setStatus(Status.SENDED.toString());
            logNotification.setCreatedAt(LocalDate.now());
            logNotification.setUpdateAt(LocalTime.now());

            if (!result.getStatusCode().is2xxSuccessful() || result.getBody().getSuccess() != 1) {
                logNotification.setStatus(Status.ERROR.toString());
            }
            userLogRepo.save(logNotification);

            log.error("save to db " + logNotification.toString());


            if (logNotification.getStatus().equals(Status.ERROR)) {
                return ResponseEntity.badRequest().body("no se pudo notificar al chofer" + logNotification.getUser());
            }
            return ResponseEntity.ok("Se envio notificacion con la info");

        } catch (IllegalArgumentException e) {
            log.error("error save notification " + e.getMessage());
            throw new RuntimeException(e.getMessage());

        } catch (JsonMappingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    private TravelNotificationMobileDTO setToken(TravelNotificationMobileDTO travelNotificationMobileDTO) {
        Optional.ofNullable(travelNotificationMobileDTO.getData().getOrDefault(Constants.CAR_DRIVER, "2")).orElse("2");
        Integer carDriver = (Integer) travelNotificationMobileDTO.getData().get(Constants.CAR_DRIVER);

        log.debug("carDriver", carDriver);
        Optional<UserNotification> userNotification = userNotiRepo.findById(Long.valueOf(carDriver));
        if (!userNotification.isPresent()) throw new UserNotExist("el chofer no se encuentra registrado" + carDriver);

        travelNotificationMobileDTO.setTo(userNotification.get().getFcmToken());
        travelNotificationMobileDTO.getData().remove("car-driver");

        return travelNotificationMobileDTO;
    }

    private HttpHeaders getHttpHeaders(String device) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (device.equals("IOS")) headers.add(HttpHeaders.AUTHORIZATION, TOKEN_IOS);
        else headers.add(HttpHeaders.AUTHORIZATION, TOKEN_ANDROID);
        log.debug("token-header", headers.get(HttpHeaders.AUTHORIZATION));
        return headers;
    }
}