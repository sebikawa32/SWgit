// GoogleLoginResponse.java
package com.jose.ticket.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleLoginResponse {
    private String token;
    private Long userId;
    private String role;
    private String nickname;
    private String provider;
}
