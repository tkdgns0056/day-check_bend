package com.project.daycheck.exception;

/**
 * 잘못된 인증 정보 예외
 */
public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException(){
        super(ErrorCode.INVALID_CREDENTIALS);
    }
}