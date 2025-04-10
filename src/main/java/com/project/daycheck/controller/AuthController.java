package com.project.daycheck.controller;

import com.project.daycheck.exception.ErrorCode;
import com.project.daycheck.dto.request.EmailVerificationRequest;
import com.project.daycheck.dto.request.MemberLoginRequestDto;
import com.project.daycheck.dto.request.MemberSignupRequestDto;
import com.project.daycheck.dto.request.VerifyCodeRequest;
import com.project.daycheck.dto.response.ApiResponseDto;
import com.project.daycheck.dto.response.TokenResponseDto;
import com.project.daycheck.exception.BusinessException;
import com.project.daycheck.service.AuthenticationService;
import com.project.daycheck.service.MemberService;
import com.project.daycheck.service.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final VerificationService verificationService;
    private final AuthenticationService authenticationService;


    /**
     * 인증 코드 발송
     */
    @PostMapping("/send-verification")
    public ResponseEntity<ApiResponseDto<Void>> sendVerificationCode(@Valid @RequestBody EmailVerificationRequest request) {

        log.info("인증 코드 발송 요청: {}", request.getEmail());

        // 이메일 중복 체크?
        if(memberService.existsByEmail(request.getEmail())){
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 인증 코드 생성 및 발송
        verificationService.generateAndSendCode(request.getEmail());

        return ResponseEntity.ok(
                ApiResponseDto.success("인증 코드가 이메일로 발송되었습니다.")
        );
    }

    /**
     * 인증 코드 확인
     */
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponseDto<Void>> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        log.info("인증 코드 확인 요청: {}", request.getEmail());

        boolean isValid = verificationService.verifyCode(request.getEmail(), request.getCode());

        if(!isValid) {
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        return ResponseEntity.ok(ApiResponseDto.success("이메일 인증이 완료되었습니다."));
    }

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<Long>> signup(@Valid @RequestBody MemberSignupRequestDto request) {

        log.info("회원가입 요청: {}", request.getEmail());

        // 이메일 인증 여부 확인
        if(!verificationService.isEmailVerified(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 회원가입 처리
        Long memberId = memberService.signUp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("회원가입이 완료되었습니다.", memberId));
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<TokenResponseDto>> login(@Valid @RequestBody MemberLoginRequestDto request) {

        log.info("로그인 요청: {}", request.getEmail());

        // 로그인 처리 및 토큰 발급
        TokenResponseDto tokenResponse = authenticationService.login(
                new MemberLoginRequestDto(request.getEmail(), request.getPassword())
        );

        return ResponseEntity.ok(ApiResponseDto.success("로그인이 완료되었습니다.", tokenResponse));
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDto<TokenResponseDto>> refresh(@RequestBody Map<String, String> request) {

        String refreshToken = request.get("refreshToken");

        if(refreshToken == null) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 토큰 갱신 처리
        TokenResponseDto tokenResponse = authenticationService.refreshToken(refreshToken);

        return ResponseEntity.ok(ApiResponseDto.success("토큰이 갱신되었습니다.", tokenResponse));
    }

}
