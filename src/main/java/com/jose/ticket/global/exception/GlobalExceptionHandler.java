package com.jose.ticket.global.exception;

import com.jose.ticket.global.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//서버에서 어떤 예외가 발생해도 이 곳에서 잡아서 공통된 에러 응답 구조(CommonResponse)로 포장해서
//HTTP 500 상태코드와 함께 클라이언트에 전달해 주는 역할을 해.


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleAllExceptions(Exception ex) {
        CommonResponse<?> response = CommonResponse.error("Internal server error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}