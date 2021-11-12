package com.transtour.backend.notification.dto;

import lombok.Data;

import java.util.Map;

@Data
public class TravelInfoNotificationMobileDTO {

    private String to;
    private Map<String, String> data;
}
