package com.transtour.backend.notification.dto;

import lombok.Data;

@Data
public class MailRequestDTO {

    // Informacion del email
    private String signature;
    private String location;
    private String to;
    private String from;

    // Informacion del viaje
    private String origin;
    private String destiny;
    private String driver;
    private String date;
    private String time;
    private String passenger;
    private String observation;
}