package com.jose.ticket.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;


 // CommonResponse 클래스
 //- API 응답을 공통 포맷으로 감싸는 제네릭 클래스
 //- 성공 여부(success), 메시지(message), 데이터(data)를 포함

@Getter
@AllArgsConstructor
public class CommonResponse<T> {
    private boolean success;  // 요청 성공 여부
    private String message;   // 응답 메시지
    private T data;           // 실제 응답 데이터

     // 에러 응답 생성 메서드

    public static CommonResponse<?> error(String errorCode, String message) {
        return new CommonResponse<>(false, message, null);
    }
}
