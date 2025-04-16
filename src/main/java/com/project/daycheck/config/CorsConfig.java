package com.project.daycheck.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;


/**
 * 리액트랑 연동 하기 위한 설정
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

//        // 프론트엔드 출처 허용
//        config.addAllowedOrigin("http://localhost:5174");
//        config.addAllowedOrigin("http://localhost:5173");

        // allowCredentials와 allowedOrigins는 함꼐 사용 불가
        // 그래서 allowedOrigins 대신 allowedOriginPatterns를 사용 해야함.
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:5174",
                "http://localhost:5173",
                "http://localhost:3000"
                ));

        // 자격 증명 허용(SSE에 필요)
        config.setAllowCredentials(true);

        // 모든 HTTP 메서드 허용
        config.addAllowedMethod("*");

        // 모든 헤더 허용 -> 구체적인 허용 메서드로 변경
//        config.addAllowedHeader("*");
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // 보안을 위해 특정 헤더만 허용
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept"
        ));

        // 노출할 헤더 지정
        config.setExposedHeaders(List.of(
                "Authorication",
                "Content-Type"
        ));

        // 모든 API 경로에 적용
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}