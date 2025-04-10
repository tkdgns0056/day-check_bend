package com.project.daycheck.exception;

public class MemberNotFoundException extends BusinessException {
    public MemberNotFoundException(){
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}
