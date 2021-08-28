package com.transtour.backend.notification.dto;

import lombok.Data;

@Data
public class MailRequestDTO {

    private String name;
    private String to;
    private String from;
    private String subject;

}