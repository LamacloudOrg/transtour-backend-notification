package com.transtour.backend.notification.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class NotificationVoucherDTO implements Serializable{
    private String travelId;
    private String passengerEmail;
}
