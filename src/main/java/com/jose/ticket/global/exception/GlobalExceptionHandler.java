package com.jose.ticket.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 전역적으로 모든 컨트롤러에서 발생하는 예외를 처리해주는 클래스임을 명시
public class GlobalExceptionHandler {

    // TicketNotFoundException 예외가 발생했을 때 이 메서드가 호출됨
    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<String> handleTicketNotFound(TicketNotFoundException e) {
        // 예외 메시지를 포함한 404 Not Found 응답을 클라이언트에게 반환
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
