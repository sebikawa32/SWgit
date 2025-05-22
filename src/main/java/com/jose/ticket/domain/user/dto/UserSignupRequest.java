package com.jose.ticket.domain.user.dto;
//dto - 회원가입 요청 

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSignupRequest {
    private String userId;  // 아이디
    private String password;
    private String name;
    private String email;
    private String nickname;
}