package com.project.daycheck.service;

import com.project.daycheck.config.security.JwtTokenProvider;
import com.project.daycheck.dto.request.MemberSignupRequestDto;
import com.project.daycheck.entity.Member;
import com.project.daycheck.entity.Role;
import com.project.daycheck.exception.*;
import com.project.daycheck.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

   public boolean existsByEmail(String email) {
       return memberRepository.existsByEmail(email);
   }

   public Long signUp(MemberSignupRequestDto requestDto) {
       log.info("회원가입 요청: {}", requestDto.getEmail());

       // 이메일 인증 여부 확인
       if(!verificationService.isEmailVerified(requestDto.getEmail())) {
           log.info("이메일 미인증: {}", requestDto.getEmail());
           throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
       }

       // 이메일 중복 확인
       if(existsByEmail(requestDto.getEmail())) {
           log.info("이메일 중복: {}", requestDto.getEmail());
           throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
       }

       // 비밀번호 암호화
       String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

       // 회원 생성
       Member member = Member.builder()
               .email(requestDto.getEmail())
               .password(encodedPassword)
               .name(requestDto.getName())
               .role(Role.USER)
               .build();

       // 이메일 인증 처리
       member.verifyEmail();

       // 저장
       Member savedMember = memberRepository.save(member);
       log.info("회원가입 완료: {}", savedMember.getEmail());

       return savedMember.getId();
   }

    /**
     * 이메일로 사용자 조회
     * @param email : 사용자 email
     * @return Member: 사용자 객체 그대로 반환
     */
    @Transactional(readOnly = true)
    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
