package com.project.daycheck.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.daycheck.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> {

    private boolean success;
    private String code;
    private String message;
    private T data;

   // 성공 응답 생성 (데이터 포함)
    public static <T> ApiResponseDto<T> success(T data) {
        return success("SUCCESS", "요청이 성공적으로 처리되었습니다.", data);
    }

    // 성공 응답 생성 (메세지만)
    public static <T> ApiResponseDto<T>  success(String message) {
        return success("SUCCESS", message, null);
    }

    // 성공 응답 생성(메시지와 데이터)
    public static <T> ApiResponseDto<T> success(String message, T data) {
        return success("SUCCESS", message, data);
    }

    // 성공 응답 생성(코드, 메시지, 데이터)
    public static <T> ApiResponseDto<T> success(String code, String message, T data){
        ApiResponseDto<T> response = new ApiResponseDto<>();
        response.success = true;
        response.code = code;
        response.message = message;
        response.data = data;
        return response;
    }

    // 에러 응답 생성(ErrorCode 활용)
    public static <T> ApiResponseDto<T> error(ErrorCode errorCode) {
        return error(errorCode, null);
    }

    // 에러 응답 생성 (ErrorCode와 데이터 활용)
    public static <T> ApiResponseDto<T> error(ErrorCode errorCode, T data){
    ApiResponseDto<T> response = new ApiResponseDto<>();
    response.success =false;
    response.code = errorCode.getCode();
    response.message = errorCode.getMessage();
    response.data = data;
    return response;

    }
}
