package com.project.daycheck.repository;

import com.project.daycheck.entity.EmailVerificationToken;
import com.project.daycheck.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByEmailAndCode(String email, String code);
    void deleteByEmail(String email);
    boolean existsByEmailAndVerifiedTrue(String email);
}
