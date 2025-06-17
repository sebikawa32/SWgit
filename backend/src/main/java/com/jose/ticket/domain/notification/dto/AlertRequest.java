package com.jose.ticket.domain.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertRequest {
    private Long ticketId;
    private int alertMinutes;
    private boolean emailEnabled;
}
