package com.project.daycheck.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenResponseDto {

    private String grantType; // 토큰 타입(Bearer)
    private String accessToken; // 엑세스 토큰
    private String refreshToken; // 리프레시 토큰
    private Long accessTokenExpiresIn;

    @Builder
    public TokenResponseDto(String grantType, String accessToken, String refreshToken, Long accessTokenExpiresIn){
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }
}
