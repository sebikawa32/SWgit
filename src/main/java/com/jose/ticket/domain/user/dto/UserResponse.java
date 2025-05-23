package com.jose.ticket.domain.user.dto;

import com.jose.ticket.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

//UserResponse DTO
 // 회원 정보를 클라이언트에 반환할 때 사용하는 객체
 // User 엔티티를 API 응답에 맞게 변환하여 전달
 // 주요 필드: id, email, nickname

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {

    // 회원 고유 식별자
    private Long id;

    // 회원 이메일 주소
    private String email;

    // 회원 닉네임
    private String nickname;

    //-엔티티를 DTO로 변환하는 팩토리 메서드
    //-@param user User 엔티티 객체
    //-@return UserResponse DTO 객체

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}