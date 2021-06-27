package com.transtour.backend.notification.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ResultDTO implements Serializable {

    private String multicast_id;
    private int success;
    private int failure;
    private int canonical_ids;
    private List<MessageDTO> message_id;
}