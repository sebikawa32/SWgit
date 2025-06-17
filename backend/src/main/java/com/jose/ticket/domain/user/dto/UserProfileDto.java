package com.jose.ticket.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jose.ticket.domain.user.entity.User;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {

    private Long id;            // ✅ PK 추가됨
    private String email;       // ✅ 표시용 (프론트에 보여주되 수정은 불가)
    private String nickname;
    private String realname;
    private String phoneNumber;
    private String provider;    // ✅ LOCAL 또는 GOOGLE

    // ✅ User → DTO 변환 메서드
    public static UserProfileDto fromEntity(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .realname(user.getRealname())
                .phoneNumber(user.getPhoneNumber())
                .provider(user.getProvider())
                .build();
    }
}
