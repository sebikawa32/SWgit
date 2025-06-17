package com.jose.ticket.domain.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {

    private String email;       // ✅ 표시용 (프론트에 보여주되 수정은 불가)

    private String nickname;
    private String realname;
    private String phoneNumber;
}
