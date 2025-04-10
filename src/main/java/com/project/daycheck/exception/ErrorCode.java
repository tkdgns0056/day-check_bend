package com.project.daycheck.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 응답 DTO
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E002", "서버 내부 오류가 발생했습니다."),

    // 회원 관련 에러
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "M001", "이미 사용 중인 이메일입니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "M002", "이메일 인증이 완료되지 않았습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M003", "존재하지 않는 회원입니다."),

    // 인증 관련 에러
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "A001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "만료된 토큰입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A003", "이메일 또는 비밀번호가 일치하지 않습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "A004", "유효하지 않은 인증 코드입니다."),

    // 이메일 관련 에러
    EMAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "이메일 발송에 실패했습니다."),

    // 입력값 검증 에러
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "V001", "이메일 형식이 올바르지 않습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "V002", "비밀번호는 영문자, 숫자, 특수문자를 포함하여 8자 이상이어야 합니다."),
    INVALID_NAME_FORMAT(HttpStatus.BAD_REQUEST, "V003", "이름은 공백 없이 2자 이상 20자 이하여야 합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
