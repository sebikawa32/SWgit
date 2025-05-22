package com.jose.ticket.domain.user.dto;

//dto-로그인요청 
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequest {
    private String userId;   // 아이디로 로그인
    private String password;
}=