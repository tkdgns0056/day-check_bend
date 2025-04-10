package com.project.daycheck.service;

import com.project.daycheck.config.security.JwtTokenProvider;
import com.project.daycheck.dto.request.MemberLoginRequestDto;
import com.project.daycheck.dto.response.TokenResponseDto;
import com.project.daycheck.entity.Member;
import com.project.daycheck.exception.BusinessException;
import com.project.daycheck.exception.EmailNotVerifiedException;
import com.project.daycheck.exception.ErrorCode;
import com.project.daycheck.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    /**
     * 로그인 처리 및 JWT 토큰 발급
     * @param requestDto 로그인 요청 데이터
     * @return 엑세스 토큰톼 리프레시 토큰
     */
    @Transactional
    public TokenResponseDto login(MemberLoginRequestDto requestDto) {
        try {
            log.info("로그인 요청: {}", requestDto.getEmail());

            // 사용자 이메일 인증 여부 확인
            Member member = memberService.findMemberByEmail(requestDto.getEmail());

            // 인증 안되었으면 에러
            if(!member.isEmailVerified()) {
                log.info("이메일 미인증: {}", requestDto.getEmail());
                throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
            }

            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    requestDto.getEmail(),
                    requestDto.getPassword()
            );

            // 실제 인증 (AuthenticationManager에 의해 처리함)
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // JWT 토큰 생성
            String accessToken = jwtTokenProvider.createAccessToken(authentication);
            String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

            // 토큰 응답 객체 생성 (빌더 패턴 활용)
            return TokenResponseDto.builder()
                    .grantType("Bearer")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 엑세스 토큰 발급
     * @param refreshToken 리프레시 토큰
     * @return 새로운 엑세스 토큰톼 리프레시 토큰
     */
    @Transactional
    public TokenResponseDto refreshToken(String refreshToken) {
        // 리프레시 토큰 유효성 검증
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            log.info("유효하지 않은 리프레시 토큰");
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 토큰에서 인증 정보 추출
        Authentication authentication =jwtTokenProvider.getAuthentication(refreshToken);

        // 새로운 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 토큰 응답 객체 생성
        return TokenResponseDto.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
