package com.project.daycheck.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception {
      return http
              // CSRF 비활성화
              .csrf(AbstractHttpConfigurer::disable)

              // CORS 설정
              .cors(cors -> cors.configurationSource(corsConfigurationSource()))

              // 세션 관리 설정 (STATELESS - JWT 사용)
              .sessionManagement(session -> session
                      .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

              // 예외 처리 설정
              .exceptionHandling(exception -> exception
                      .authenticationEntryPoint(jwtAuthenticationEntryPoint) //인증 실패
                      .accessDeniedHandler(jwtAccessDeniedHandler)) // 인가 실패

              // 요청 권한 설정
              .authorizeHttpRequests(authorize -> authorize
                      .requestMatchers("/api/auth/**").permitAll()
                      .requestMatchers("/api/admin/**").hasRole("ADMIN")
                      .requestMatchers("/api/members/**").authenticated()
                      .anyRequest().authenticated())

              // HTTP 기본 인증 비활성화
              .httpBasic(AbstractHttpConfigurer::disable)

              // 폼 기반 로그인 비활성화
              .formLogin(AbstractHttpConfigurer::disable)

              // JWT 필터 추가
              .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                      UsernamePasswordAuthenticationFilter.class)

              .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:3000"
        )); // 리액트 서버
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
