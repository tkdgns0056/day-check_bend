package com.project.daycheck.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 이메일 인증 코드 요청 DTO
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

}
