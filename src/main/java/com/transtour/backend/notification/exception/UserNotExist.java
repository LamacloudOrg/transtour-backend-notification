package com.transtour.backend.notification.exception;

public class UserNotExist extends RuntimeException{

    public UserNotExist(){
        super("Usuario No existe");
    }
}
