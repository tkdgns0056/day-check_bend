package com.project.daycheck.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenRefreshRequest {

    @NotBlank(message = "리프레시 토큰은 필수 입력값입니다.")
    private String refreshToken;
}
