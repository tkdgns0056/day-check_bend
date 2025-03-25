package com.project.daycheck.service;

import com.project.daycheck.dto.request.MemberSignupRequestDto;
import com.project.daycheck.entity.EmailVerificationToken;
import com.project.daycheck.entity.Member;
import com.project.daycheck.entity.Role;
import com.project.daycheck.exception.DuplicateEmailException;
import com.project.daycheck.exception.InvalidTokenException;
import com.project.daycheck.exception.MemberNotFoundException;
import com.project.daycheck.repository.EmailVerificationTokenRepository;
import com.project.daycheck.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * 사용자 회원가입 처리
     * @param requestDto : 회원가입 요청 데이터
     * @return 생성된 사용자 ID
     */
    @Transactional
    public Long singUp(MemberSignupRequestDto requestDto) {
        // 이메일 중복 확인
        if(memberRepository.existsByEmail(requestDto.getEmail())){
            throw new DuplicateEmailException("이미 사용중인 이메일입니다: " + requestDto.getEmail());
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 사용자 생성(빌더 패턴 활용)
        Member member = Member.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .name(requestDto.getName())
                .role(Role.USER)
                .build();

        // 사용자 저장
        Member savedMember = memberRepository.save(member);

        // 이메일 인증 토큰 생성 - ddd 패턴
        EmailVerificationToken verificationToken = EmailVerificationToken.createTokenForUser(savedMember);
        tokenRepository.save(verificationToken);

        // 저장하고 나서 이메일 인증 받아야 최종 회원가입 되므로, 이메일 발송 추가
        // 유저 정보 + 생성한 토큰 인증
        emailService.sendVerificationEmail(savedMember, verificationToken.getToken());

        return savedMember.getId();
    }

    /**
     * 이메일 토큰 확인 및 인증 처리
     * @param token : 이메일 인증 토큰
     * @return : 인증된 사용자 ID
     */
    @Transactional
    public Long verifyEmail(String token) {
        // 토큰 조회 (Optionnal)
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 인증 토큰입니다."));

        // 토큰 만료 확인
        if(verificationToken.isExpired()) {
            throw new InvalidTokenException("만료된 인증 토큰입니다.");
        }

        // 사용자 조회 및 이메일 인증 처리 - 인증된 사용자 저장
        Member member = verificationToken.getMember();
        member.verifyEmail();
        memberRepository.save(member);

        // 사용한 토큰 삭제 ? (왜 토큰 삭제하지? 유효해야하지 않나..?)
        tokenRepository.delete(verificationToken);

        return member.getId();
    }

    /**
     * 이메일로 사용자 조회
     * @param email : 사용자 email
     * @return Member: 사용자 객체 그대로 반환
     */
    @Transactional(readOnly = true)
    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다:" + email));
    }
}
