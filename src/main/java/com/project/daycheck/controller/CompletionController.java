package com.project.daycheck.controller;

import com.project.daycheck.dto.CompletionHistoryDTO;
import com.project.daycheck.dto.request.CompletionToggleRequest;
import com.project.daycheck.entity.CompletionHistory;
import com.project.daycheck.service.CompletionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/schedules/completion")
@RequiredArgsConstructor
public class CompletionController {

    private final CompletionHistoryService completionHistoryService;


    @Operation(summary = "일정 완료 상태 토글", description = "일반 또는 반복 일정의 완료 상태를 토글합니다.")
    @PostMapping("/toggle")
    public ResponseEntity<CompletionHistoryDTO> toggleCompletion(@Valid  @RequestBody CompletionToggleRequest request){

        log.info("일정 완료 상태 토글 요청: 일정 ID={}, 날짜={}, 반복여부={}", request.getScheduleId(), request.getDate(), request.getIsRecurring());

        CompletionHistoryDTO result = completionHistoryService.toggleCompletion(
                request.getScheduleId(),
                request.getDate(),
                request.getIsRecurring());

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "날짜별 완료 이력 조회", description = "특정 날짜의 모든 일정 완료 이력을 조회합니다.")
    @GetMapping("/date/{date}")
    public ResponseEntity<List<CompletionHistoryDTO>> getCompletionByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){

        List<CompletionHistoryDTO> completions = completionHistoryService.getCompletionByDate(date);

        return ResponseEntity.ok(completions);
    }

    @Operation(summary = "일정별 완료 이력 조회", description = "특정 일정의 모든 완료 이력을 조회합니다.")
    @GetMapping("/schedule/{id}")
    public ResponseEntity<List<CompletionHistoryDTO>> getCompletionsBySchedule(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") Boolean isRecurring) {

        log.info("일정별 완료 이력 조회: 일정 ID={}, 반복여부={}", id, isRecurring);

        List<CompletionHistoryDTO> completions =
                completionHistoryService.getCompletionsBySchedule(id, isRecurring);

        return ResponseEntity.ok(completions);
    }
}
