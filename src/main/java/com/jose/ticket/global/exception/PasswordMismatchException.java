package com.jose.ticket.global.exception;

/**PasswordMismatchException 클래스
 - 비밀번호 불일치 시 발생하는 사용자 정의 런타임 예외 */
public class PasswordMismatchException extends RuntimeException {

    /**예외 생성자**/
    public PasswordMismatchException(String message) {
        super(message);
    }
}
