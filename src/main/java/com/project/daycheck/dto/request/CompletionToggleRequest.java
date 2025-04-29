package com.project.daycheck.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionToggleRequest {

    @NotNull(message = "일정 ID는 필수 입력값입니다.")
    private Long scheduleId;

    @NotNull(message = "날짜는 필수 입력값입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "반복 일정 여부는 필수 입력값입니다.")
    private Boolean isRecurring;

}
