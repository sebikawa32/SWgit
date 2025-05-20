package com.jose.ticket.global.response;
//이 클래스는 API 응답을 통일해서 보내기 위한 공통 응답 DTO 역할을 합니다.
//예외 처리 시 에러 메시지를 깔끔하게 JSON 형식으로 클라이언트에게 보내주려고 만듭니다.

public class CommonResponse<T> {

    private boolean success;
    private String message;
    private T data;

    // 생성자
    public CommonResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // 성공 응답용 팩토리 메서드
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, "Success", data);
    }

    // 에러 응답용 팩토리 메서드
    public static <T> CommonResponse<T> error(String message, T data) {
        return new CommonResponse<>(false, message, data);
    }

    // getter, setter

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}