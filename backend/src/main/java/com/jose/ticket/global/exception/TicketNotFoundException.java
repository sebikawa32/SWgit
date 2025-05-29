package com.jose.ticket.global.exception;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(Long id) {
        super("해당 ID의 티켓이 존재하지 않습니다: " + id);
    }
}
