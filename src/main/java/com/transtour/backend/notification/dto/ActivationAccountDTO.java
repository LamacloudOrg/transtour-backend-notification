package com.transtour.backend.notification.dto;

public class ActivationAccountDTO {
    private final Long driver;
    private final String code;

    public ActivationAccountDTO(Long driver, String code) {
        this.driver = driver;
        this.code = code;
    }

    public Long getDriver() {
        return driver;
    }

    public String getCode() {
        return code;
    }
}
