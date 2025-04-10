package com.project.daycheck.exception;

/**
 * 중복된 이메일 예외
 */
public class DuplicateEmailException extends BusinessException {
   public  DuplicateEmailException(){
       super(ErrorCode.DUPLICATE_EMAIL);
   }
}





