package com.project.daycheck.exception;

/**
 * 이메일 인증이 완료되지 않은 예외
 */
public class EmailNotVerifiedException extends BusinessException {
    public EmailNotVerifiedException() {
        super(ErrorCode.EMAIL_NOT_VERIFIED);
    }
}