package com.jose.ticket.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static CommonResponse<?> error(String errorCode, String message) {
        // errorCode는 현재 사용하지 않으니 필요하면 나중에 추가하세요
        return new CommonResponse<>(false, message, null);
    }
}
