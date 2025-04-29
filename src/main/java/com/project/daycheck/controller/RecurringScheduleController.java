package com.project.daycheck.controller;

import com.project.daycheck.dto.RecurringExceptionDTO;
import com.project.daycheck.dto.RecurringScheduleDTO;
import com.project.daycheck.dto.ScheduleDTO;
import com.project.daycheck.dto.request.RecurringExceptionRequest;
import com.project.daycheck.dto.request.RecurringScheduleRequest;
import com.project.daycheck.service.RecurringScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "반복 일정 관리 컨트롤러", description = "반복 일정 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/schedules/recurring")
@RequiredArgsConstructor
public class RecurringScheduleController {

    private final RecurringScheduleService recurringScheduleService;

    @Operation(summary = "반복 일정 목록 조회", description = "모든 반복 일정 패턴 조회")
    @GetMapping
    public ResponseEntity<List<RecurringScheduleDTO>> getAllRecurringSchedules() {
        log.info("반복 일정 목록 조회");
        List<RecurringScheduleDTO> recurringSchedules = recurringScheduleService.getAllRecurringSchedules();
        return ResponseEntity.ok(recurringSchedules);
    }

    @Operation(summary = "반복 일정 상세 조회", description = "특정 반복 일정 패턴 조회")
    @GetMapping("/{id}")
    public ResponseEntity<RecurringScheduleDTO> getRecurringScheduleById(@PathVariable Long id) {
        log.info("반복 일정 상세 조회: {}", id);
        RecurringScheduleDTO recurringSchedule = recurringScheduleService.getRecurringScheduleById(id);
        return ResponseEntity.ok(recurringSchedule);
    }

    @Operation(summary = "특정 날짜의 반복 일정 조회", description = "특정 날짜에 해당하는 모든 반복 일정 조회")
    @GetMapping("/date/{date}")
    public ResponseEntity<List<ScheduleDTO>> getRecurringSchedulesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("특정 날짜의 반복 일정 조회: {}", date);
        List<ScheduleDTO> schedules = recurringScheduleService.getRecurringSchedulesByDate(date);
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "기간별 반복 일정 조회", description = "특정 기간 내 모든 반복 일정 조회")
    @GetMapping("/range")
    public ResponseEntity<List<ScheduleDTO>> getRecurringSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("기간별 반복 일정 조회: {} ~ {}", startDate, endDate);
        List<ScheduleDTO> schedules = recurringScheduleService.getRecurringSchedulesByDateRange(startDate, endDate);
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "반복 일정 생성", description = "새로운 반복 일정 패턴 생성")
    @PostMapping
    public ResponseEntity<RecurringScheduleDTO> createRecurringSchedule(@Valid @RequestBody RecurringScheduleRequest request) {
        log.info("반복 일정 생성: {}", request.getContent());
        RecurringScheduleDTO recurringSchedule = recurringScheduleService.createRecurringSchedule(request);
        return ResponseEntity.ok(recurringSchedule);
    }

    @Operation(summary = "반복 일정 수정", description = "특정 반복 일정 패턴 수정")
    @PutMapping("/{id}")
    public ResponseEntity<RecurringScheduleDTO> updateRecurringSchedule(
            @PathVariable Long id,
            @Valid @RequestBody RecurringScheduleRequest request) {
        log.info("반복 일정 수정: {}", id);
        RecurringScheduleDTO recurringSchedule = recurringScheduleService.updateRecurringSchedule(id, request);
        return ResponseEntity.ok(recurringSchedule);
    }

    @Operation(summary = "반복 일정 삭제", description = "특정 반복 일정 패턴 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurringSchedule(@PathVariable Long id) {
        log.info("반복 일정 삭제: {}", id);
        recurringScheduleService.deleteRecurringSchedule(id);
        return ResponseEntity.noContent().build();
    }
}