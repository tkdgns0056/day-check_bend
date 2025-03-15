package com.project.daycheck.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * 리액트랑 연동 하기 위한 설정
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 프론트엔드 출처 허용
        config.addAllowedOrigin("http://localhost:5174");
        config.addAllowedOrigin("http://localhost:5173");

        // 모든 HTTP 메서드 허용
        config.addAllowedMethod("*");

        // 모든 헤더 허용
        config.addAllowedHeader("*");

        // 모든 API 경로에 적용
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}