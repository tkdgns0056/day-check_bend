package com.project.daycheck.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 반복 일정 예외 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurringExceptionRequest {

    @NotNull(message = "반복 일정 ID는 필수 입력값입니다")
    private Long recurringScheduleId;

    @NotNull(message = "예외 날짜는 필수 입력값입니다")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate exceptionDate;

    @NotBlank(message = "예외 유형은 필수 입력값입니다")
    private String exceptionType; // SKIP(건너뛰기), MODIFY(수정)

    // 수정 정보 (exceptionType이 MODIFY인 경우)
    private String modifiedTitle;
    private String modifiedStartTime;
    private String modifiedEndTime;
    private String modifiedPriority;
    private String modifiedDescription;
}