package com.transtour.backend.notification.controller;

import com.transtour.backend.notification.dto.*;
import com.transtour.backend.notification.service.FirebaseMessagingService;
import com.transtour.backend.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RestController
@RequestMapping("/v1/notification")
public class NotificationController {

    private static Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    NotificationService service;

    @Autowired
    FirebaseMessagingService firebaseService;


    private static Function<Void, ResponseEntity> emailOk = n -> {
        log.error("Se notifico un nuevo viaje");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    };

    @PostMapping("/sendMessageMobile")
    @Async
    private ResponseEntity sendNotificationMobile(@RequestBody TravelNotificationMobileDTO travelNotificationMobileDTO) throws IOException {
        ResponseEntity result = firebaseService.sendNotification(travelNotificationMobileDTO);
        log.debug("se notifico x firebase");
        return result;
    }

    @PostMapping("/registerToken")
    private CompletableFuture<String> registerToken(@RequestBody UserNotificationDTO userNotificationDTO) {
        return service.registerToken(userNotificationDTO);
    }

    @PostMapping("/sendingEmail")
    public MailResponseDTO sendEmail(@RequestBody MailRequestDTO request) {
        Map<String, Object> model = new HashMap<>();
        model.put("origin", request.getOrigin());
        model.put("destiny", request.getDestiny());
        model.put("driver", request.getDriver());
        model.put("date", request.getDate());
        model.put("time", request.getTime());
        model.put("passengerName", request.getPassengerName());
        model.put("observation", request.getObservation());

        return service.sendEmail(request, model);

    }

    @PostMapping("/activation/code")
    public CompletableFuture<MailResponseDTO> sendCodeByMail(@RequestBody ActivationAccountDTO activationAccountDTO) {
        return service.sendCodeNotifcation(activationAccountDTO);
    }

    @PostMapping("/sendPdfToPassenger")
    public CompletableFuture<String> sendPdfToPassenger(@RequestBody NotificationVoucherDTO notificationVoucherDTO) {
        return service.sendPdfToPassenger(notificationVoucherDTO);
    }

}
