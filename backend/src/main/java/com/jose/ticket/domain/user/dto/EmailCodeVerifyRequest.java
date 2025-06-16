package com.jose.ticket.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailCodeVerifyRequest {
    private String email;
    private String code;
}
