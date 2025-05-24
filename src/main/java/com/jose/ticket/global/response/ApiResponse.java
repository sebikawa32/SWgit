package com.jose.ticket.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**ApiResponse 클래스
 - API 응답 시 메시지와 데이터를 담는 공통 응답 객체*/
@Getter
@AllArgsConstructor
public class ApiResponse {

    // 응답 메시지
    private String message;

    // 응답 데이터 (유동적인 타입 가능)
    private Object data;
}
