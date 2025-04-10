package com.project.daycheck.service;

import com.project.daycheck.entity.Member;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationCode(String email, String code) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(email);
            helper.setFrom("tkdgns0056@gmail.com");
            helper.setSubject("이메일 인증 코드");

            String emailContent =
                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eaeaea; border-radius: 5px;'>" +
                            "<h1 style='color: #4a90e2;'>이메일 인증 코드</h1>" +
                            "<p>안녕하세요! 회원가입을 위한 인증 코드입니다:</p>" +
                            "<div style='background-color: #f2f2f2; padding: 15px; text-align: center; font-size: 24px; letter-spacing: 5px; margin: 20px 0; border-radius: 4px;'>" +
                            "<strong>" + code + "</strong>" +
                            "</div>" +
                            "<p>이 코드는 10분 동안 유효합니다.</p>" +
                            "<p>코드를 요청하지 않으셨다면 이 메일을 무시해주세요.</p>" +
                            "<p>감사합니다.</p>" +
                            "</div>";
            helper.setText(emailContent, true);

            log.info("이메일 발송 시도:{}", email);
            mailSender.send(mimeMessage);
            log.info("이메일 발송 성공:{}", email);

        } catch (MessagingException e) {
            log.error("이메일 발송 실패: {} - {}", email, e.getMessage());
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.", e);
        }
    }

//    @Async
//    public void sendVerificationEmail(Member member, String token) {
//        try {
//            // 추후 리액트 구현 시 리엑트 서버로 url 수정 필요.
//            String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + token;
//
//            // 이메일 메시지 생성
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
//            helper.setTo(member.getEmail());
//            helper.setSubject("이메일 주소 인증");
//
//            // HTML 이메일 내용 구성
//            String emailContent =
//                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eaeaea; border-radius: 5px;'>" +
//                            "<h1 style='color: #333; text-align: center;'>이메일 주소 인증</h1>" +
//                            "<p>" + member.getName() + "님, 회원가입해 주셔서 감사합니다.</p>" +
//                            "<p>아래 버튼을 클릭하여 이메일 주소를 인증해 주세요.</p>" +
//                            "<div style='text-align: center; margin: 30px 0;'>" +
//                            "<a href='" + verificationUrl + "' style='background-color: #4CAF50; color: white; padding: 12px 20px; text-decoration: none; border-radius: 4px; font-weight: bold;'>이메일 인증하기</a>" +
//                            "</div>" +
//                            "<p>또는 아래 링크를 브라우저에 복사하여 인증할 수 있습니다:</p>" +
//                            "<p style='word-break: break-all;'>" + verificationUrl + "</p>" +
//                            "<p>이 링크는 24시간 동안 유효합니다.</p>" +
//                            "<p>본인이 요청하지 않은 경우 이 메일을 무시해 주세요.</p>" +
//                            "</div>";
//
//            helper.setText(emailContent, true);
//
//            // 메일 발송
//            mailSender.send(mimeMessage);
//
//        } catch (MessagingException e) {
//            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.", e);
//
//        }
//    }
}
