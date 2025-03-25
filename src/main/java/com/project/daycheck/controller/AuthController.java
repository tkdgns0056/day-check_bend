package com.project.daycheck.controller;

import com.project.daycheck.dto.request.MemberLoginRequestDto;
import com.project.daycheck.dto.request.MemberSignupRequestDto;
import com.project.daycheck.dto.response.TokenResponseDto;
import com.project.daycheck.service.AuthenticationService;
import com.project.daycheck.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final AuthenticationService authenticationService;

    /**
     * 회원가입 API
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody MemberSignupRequestDto requestDto){
        Long memberId = memberService.singUp(requestDto);

        Map<String, Object> response = new HashMap<>();
        response.put("memberId", memberId);
        response.put("message", "회원가입이 완료되었습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 이메일 인증 API
     */
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam String token) {
        Long memberId = memberService.verifyEmail(token);

        Map<String, Object> response = new HashMap<>();
        response.put("memberId", memberId);
        response.put("message", "이메일 인증이 완료되었습니다.");
        response.put("verified", true);

        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 API
     */
    @PostMapping("login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody MemberLoginRequestDto requestDto){
        TokenResponseDto tokenResponseDto = authenticationService.login(requestDto);
        return ResponseEntity.ok(tokenResponseDto);
    }

    /**
     * 토큰 갱신 API
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        TokenResponseDto tokenResponseDto = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(tokenResponseDto);
    }
}
