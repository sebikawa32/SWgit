package com.jose.ticket.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice // 전역적으로 모든 컨트롤러에서 발생하는 예외를 처리해주는 클래스임을 명시
public class GlobalExceptionHandler {

    // ✅ TicketNotFoundException 처리
    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<String> handleTicketNotFound(TicketNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    // ✅ 회원가입/로그인 등에서 발생하는 유효성 검사 오류 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        String errorMessage = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> {
                    String field = fieldError.getField();
                    String message = fieldError.getDefaultMessage();
                    return field + ": " + message;
                })
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // ✅ 일반적인 RuntimeException 처리 (예: 비밀번호 불일치 등)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
