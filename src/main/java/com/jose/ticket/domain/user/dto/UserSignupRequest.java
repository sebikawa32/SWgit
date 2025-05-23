package com.jose.ticket.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSignupRequest {

    @NotBlank(message = "아이디는 필수입니다")
    private String userId;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "비밀번호는 8자 이상, 영문자와 숫자를 포함해야 합니다")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String passwordConfirm;

    @AssertTrue(message = "비밀번호가 일치하지 않습니다")
    public boolean isPasswordMatching() {
        return password != null && password.equals(passwordConfirm);
    }

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @Email(message = "유효한 이메일 주소를 입력해주세요")
    @NotBlank(message = "이메일은 필수입니다")
    private String email;

    @NotBlank(message = "닉네임은 필수입니다")
    private String nickname;

}
