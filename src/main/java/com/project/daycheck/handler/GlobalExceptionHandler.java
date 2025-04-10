package com.project.daycheck.handler;

import com.project.daycheck.exception.ErrorCode;
import com.project.daycheck.dto.response.ApiResponseDto;
import com.project.daycheck.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDto<Object>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("[{}] {}", errorCode.getCode(), e.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponseDto.error(errorCode));
    }

    /**
     * 입력값 검증 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Map<String, String>>> handleValidationException(MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();

        Map<String, String> fieldErrors = new HashMap<>();

        bindingResult.getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
                );

        log.error("[{}] 입력값 검증 실패: {}", ErrorCode.INVALID_INPUT_VALUE.getCode(), fieldErrors);

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponseDto.error(ErrorCode.INVALID_INPUT_VALUE, fieldErrors));
    }

    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Object>> handleException(Exception e){
        log.error("[{}] 서버 오류: {}", ErrorCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
