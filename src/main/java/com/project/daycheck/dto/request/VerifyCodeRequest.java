package com.project.daycheck.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 인증 코드 확인 DTO
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerifyCodeRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "인증 코드는 필수 입력값입니다.")
    private String code;
}
