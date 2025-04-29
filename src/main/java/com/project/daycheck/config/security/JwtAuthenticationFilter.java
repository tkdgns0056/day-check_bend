package com.project.daycheck.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * HTTP 요청을 가로채서 JWT 토큰 인증 처리
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 요청 헤더 전체 로깅
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            log.debug("Header: {} = {}", headerName, request.getHeader(headerName));
        }

        // 요청 헤더에서 JWT 토큰 추츨
        String jwt = resolverToken(request);
        log.debug("추출된 JWT 토큰: {}", jwt);

        // 토큰이 유효하면 인증 정보 설정
        if(StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
            try {
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                log.debug("생성된 Authentication: {}", authentication);

                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.error("Authentication is null after token validation");
                }
            } catch (Exception e) {
                log.error("Authentication 생성 중 예외 발생", e);
            }
        } else {
            log.warn("토큰 검증 실패 또는 토큰 없음");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 토큰 값 추출
     * @param request HTTP 요청
     * @return 추출된 토큰 문자열
     */
    private String resolverToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        // 토큰 로그 추가
        log.debug("Authorization 헤더: {}", bearerToken);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            String token = bearerToken.substring(7); // "Bearer 제거"
            log.debug("추출된 토큰: {}", token);
            return bearerToken.substring(7); // "Bearer " 제거
        }

        return null;
    }
}
