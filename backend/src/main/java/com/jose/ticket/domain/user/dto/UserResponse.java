package com.jose.ticket.domain.user.dto;

import com.jose.ticket.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** UserResponse DTO
 - 회원 정보를 클라이언트에 반환할 때 사용하는 객체
 - User 엔티티를 API 응답에 맞게 변환하여 전달
 - 주요 필드: id, email, nickname, realname, phoneNumber 등 **/
@Getter
@Builder
@AllArgsConstructor
public class UserResponse {

    private Long id;            // 회원 고유 식별자
    private String email;       // 이메일
    private String nickname;    // 닉네임
    private String realname;    // 실명
    private String phoneNumber; // 전화번호

    /** 엔티티를 DTO로 변환하는 팩토리 메서드 **/
    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .realname(user.getRealname())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
