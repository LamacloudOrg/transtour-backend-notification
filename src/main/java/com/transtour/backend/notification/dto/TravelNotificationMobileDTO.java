package com.transtour.backend.notification.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class TravelNotificationMobileDTO implements Serializable {

    private String to;
 // private Map<String, String> notification;
    private Map<String, String> data;
}
