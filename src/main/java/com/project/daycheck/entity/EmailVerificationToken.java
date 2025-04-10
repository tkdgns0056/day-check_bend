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

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean verified;

    @Builder
    public EmailVerificationToken(String email, String code) {
        this.email = email;
        this.code = code;
        this.expiryDate = LocalDateTime.now().plusMinutes(10); // 10분 유효
        this.verified = false;
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public void verify() {
        this.verified = true;
    }
}
