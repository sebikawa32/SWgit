package com.jose.ticket.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**UserSignupRequest DTO
  - 회원가입 요청 시 클라이언트가 전달하는 데이터 구조
  - 필드별 유효성 검증 어노테이션을 통해 입력값 검증 수행
  - 비밀번호 일치 여부를 커스텀 검증 메서드로 체크 **/

@Getter
@Setter
@NoArgsConstructor
public class UserSignupRequest {

    // 회원가입 시 사용할 아이디 (필수, 빈값 불가)
    @NotBlank(message = "아이디는 필수입니다")
    private String userId;

    // 비밀번호 (필수, 8자 이상, 영문+숫자 포함)
    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "비밀번호는 8자 이상, 영문자와 숫자를 포함해야 합니다")
    private String password;

    // 비밀번호 확인 (필수)
    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String passwordConfirm;

    /** 비밀번호와 비밀번호 확인 일치 여부 검증**/

    @AssertTrue(message = "비밀번호가 일치하지 않습니다")
    public boolean isPasswordMatching() {
        return password != null && password.equals(passwordConfirm);
    }

    // 사용자 이름 (필수)
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    // 이메일 (필수, 유효한 이메일 형식)
    @Email(message = "유효한 이메일 주소를 입력해주세요")
    @NotBlank(message = "이메일은 필수입니다")
    private String email;

    // 닉네임 (필수)
    @NotBlank(message = "닉네임은 필수입니다")
    private String nickname;

    // 실명 (필수)
    @NotBlank(message = "실명은 필수입니다")
    private String realname;

    // 전화번호 (필수, 10~11자리 숫자)
    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리 숫자여야 합니다")
    private String phoneNumber;
}
