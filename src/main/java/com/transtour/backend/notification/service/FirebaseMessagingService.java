package com.transtour.backend.notification.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.transtour.backend.notification.controller.NotificationController;
import com.transtour.backend.notification.dto.TravelNotificationMobileDTO;
import com.transtour.backend.notification.dto.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@Service
public class FirebaseMessagingService {

    private static Logger log = LoggerFactory.getLogger(NotificationController.class);
    private static final String SEND_NOTIFICATION_TO_MOBILE = "https://fcm.googleapis.com/fcm/send";

    private RestTemplate restTemplate;

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
        headers.add(HttpHeaders.AUTHORIZATION, "key=AAAAOYz5Ol8:APA91bEoj573SYVQKRK-LLX7dgzoVoUGsv_JVsY3QgwnzmsF0tVpp_23fMNfNsGvXDzyI0QoNJrxuWLypG_5poMpOLWXSiSnH17p5vAoaTdBuZeZBG_8T08v4EUK2vPV-S4yTzHWXawe");

        HttpEntity<TravelNotificationMobileDTO> entity = new HttpEntity<>(travelNotificationMobileDTO, headers);
        ResponseEntity<ResultDTO> result = restTemplate.postForEntity(SEND_NOTIFICATION_TO_MOBILE, entity, ResultDTO.class);

        return  result;
    }

}