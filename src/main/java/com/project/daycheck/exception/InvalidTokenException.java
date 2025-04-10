package com.project.daycheck.exception;

/**
 * 유효하지 않은 토큰 예외
 */
public class InvalidTokenException extends BusinessException {
   public InvalidTokenException(){
       super(ErrorCode.INVALID_TOKEN);
   }
}