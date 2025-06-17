package com.jose.ticket.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleSignupRequest {
    private Long id;       // PK(Long) 필드 추가
    private String userId;
    private String nickname;
    private String realname;
    private String phoneNumber;
}
