package com.transtour.backend.notification.dto;

import lombok.Data;

@Data
public class MailRequestDTO {

    // Informacion del viaje
    private String origin;
    private String destiny;
    private Long driver;
    private String date;
    private String time;
    private String passengerName;
    private String observation;
}