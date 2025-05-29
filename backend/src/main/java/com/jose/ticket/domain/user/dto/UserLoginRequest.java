package com.jose.ticket.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** UserLoginRequest DTO
  - 로그인 요청 시 클라이언트가 서버로 보내는 데이터 구조
  - 사용자 아이디(userId)와 비밀번호(password)를 포함
  - 스프링이 JSON 요청 바디를 이 객체로 매핑하여 사용  **/

@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequest {

    // 로그인에 사용할 사용자 아이디
    private String userId;

    // 로그인에 사용할 비밀번호
    private String password;
}
