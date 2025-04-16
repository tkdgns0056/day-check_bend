package com.project.daycheck.exception;

/**
 * 사용자를 찾을 수 없는 예외
 */
public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}