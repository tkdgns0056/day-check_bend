package com.project.daycheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.daycheck.entity.CompletionHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompletionHistoryDTO {


    private Long id;
    private Long scheduleId;
    private Boolean isRecurring;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate completionDate;

    private Boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity를 DTO로 변환하는 정적 메소드
    public static CompletionHistoryDTO fromEntity(CompletionHistory entity) {
        return CompletionHistoryDTO.builder()
                .id(entity.getId())
                .scheduleId(entity.getScheduleId())
                .isRecurring(entity.getIsRecurring())
                .completionDate(entity.getCompletionDate())
                .completed(entity.getCompleted())
                .createdAt(entity.getCreateAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
