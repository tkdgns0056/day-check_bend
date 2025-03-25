package com.project.daycheck.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationToken extends BaseTimeEntity{

    private static final int EXPIRATION_HOURS = 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Builder
    private EmailVerificationToken(Member member) {
        this.token = UUID.randomUUID().toString();
        this.member = member;
        this.expiryDate = LocalDateTime.now().plusHours(EXPIRATION_HOURS);
    }

    // 토큰 생성 팩토리 메서드
    public static EmailVerificationToken createTokenForUser(Member member) {
        return EmailVerificationToken.builder()
                .member(member)
                .build();
    }

    // 토큰 만료 여부 확인
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

}
