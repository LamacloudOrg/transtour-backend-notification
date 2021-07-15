package com.transtour.backend.notification.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.transtour.backend.notification.dto.TravelNotificationMobileDTO;
import com.transtour.backend.notification.dto.UserNotificationDTO;
import com.transtour.backend.notification.model.UserNotification;
import com.transtour.backend.notification.service.FirebaseMessagingService;
import com.transtour.backend.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RestController
@RequestMapping("/v1/notification")
public class NotificationController {

    private  static Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    NotificationService service;

    @Autowired
    FirebaseMessagingService firebaseService;

    @PostMapping("/byEmail")
    public CompletableFuture<ResponseEntity> sendMail(@RequestBody String message) throws Exception{
      return service.sendMail(message).<ResponseEntity>thenApply(emailOk);
    }

    private static Function<Void, ResponseEntity> emailOk = n -> {
        log.error("Se notifico un nuevo viaje");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    };

    @PostMapping("/sendMessageMobile")
    private ResponseEntity sendNotificationMobile(@RequestBody TravelNotificationMobileDTO travelNotificationMobileDTO) throws IOException {
        ResponseEntity result = firebaseService.sendNotification(travelNotificationMobileDTO);
        return result;
    }

    @PostMapping("/registerToken")
    private CompletableFuture<String> registerToken(@RequestBody UserNotificationDTO userNotificationDTO) {
        return service.registerToken(userNotificationDTO);
    }
}
