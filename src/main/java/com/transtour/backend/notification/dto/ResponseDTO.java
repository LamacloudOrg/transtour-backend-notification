package com.transtour.backend.notification.dto;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public class ResponseDTO {

    private String error;
    private HttpStatus Status;
}
