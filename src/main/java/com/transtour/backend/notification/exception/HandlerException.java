package com.transtour.backend.notification.exception;

import com.transtour.backend.notification.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HandlerException {

    @ExceptionHandler({EmailException.class})
    public ResponseEntity<ResponseDTO> emailException(Exception e) {
        ResponseDTO response = new ResponseDTO(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<ResponseDTO>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
