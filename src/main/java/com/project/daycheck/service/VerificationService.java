package com.project.daycheck.service;

import com.project.daycheck.entity.EmailVerificationToken;
import com.project.daycheck.repository.EmailVerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VerificationService {

    private final EmailVerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    public String generateAndSendCode(String email) {
        log.info("인증 코드 생성 및 발송: {}", email);

        // 기존 코드 삭제
        verificationTokenRepository.deleteByEmail(email);

        // 랜덤 6자리 코드 생성
        String code = generateRandomCode();

        // DB에 저장
        EmailVerificationToken verificationCode = EmailVerificationToken.builder()
                .email(email)
                .code(code)
                .build();

        verificationTokenRepository.save(verificationCode);

        // 이메일 발송
        emailService.sendVerificationCode(email, code);

        return code;
    }

    private String generateRandomCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(100000));
    }

    public boolean verifyCode(String email, String code) {
        log.info("인증 코드 확인: {}", email);

        Optional<EmailVerificationToken> verificationOpt = verificationTokenRepository.findByEmailAndCode(email, code);

        if(verificationOpt.isEmpty()) {
            log.info("인증 코드 없음: {}", email);
            return false;
        }

        EmailVerificationToken verification = verificationOpt.get();

        if(verification.isExpired()){
            log.info("인증 코드 만료됨: {}", email);
            return false;
        }

        // 인증 완료 처리
        verification.verify();
        verificationTokenRepository.save(verification);

        log.info("인증 성공: {}", email);
        return true;
    }

    public boolean isEmailVerified(String email) {
        return verificationTokenRepository.existsByEmailAndVerifiedTrue(email);
    }
}
