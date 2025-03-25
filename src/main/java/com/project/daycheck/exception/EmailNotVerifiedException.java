package com.project.daycheck.exception;

/**
 * 이메일 인증이 완료되지 않은 예외
 */
public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException(String message) {
        super(message);
    }
}