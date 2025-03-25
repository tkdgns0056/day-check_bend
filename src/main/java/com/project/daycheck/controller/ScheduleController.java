package com.project.daycheck.controller;

import com.project.daycheck.dto.ScheduleDTO;
import com.project.daycheck.dto.ScheduleRequest;
import com.project.daycheck.entity.Schedules;
import com.project.daycheck.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "일반 일정 관리 컨트롤러", description = "일반 일저 관리 API")
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 특정 날짜 일정 조회
    @Operation(summary = "일정 조회", description = "등록한 일정을 조회한다.")
    @GetMapping("/{date}")
    public ResponseEntity<List<Schedules>> getSchedulesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Schedules> schedules = scheduleService.getSchedulesByDate(date);
        return ResponseEntity.ok(schedules);
    }

    // 일정 추가
    @PostMapping
    public ResponseEntity<Schedules> addSchedule(@RequestBody ScheduleDTO request) {
        Schedules savedSchedule = scheduleService.addSchedule(request);
        return ResponseEntity.ok(savedSchedule);
    }

    // 일정 수정 (모든 필드 수정 가능)
    @PutMapping("/{id}")
    public ResponseEntity<Schedules> updateSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleDTO request) {
        Schedules updatedSchedule = scheduleService.updateSchedule(id, request);
        return ResponseEntity.ok(updatedSchedule);
    }

    // 일정 완료 상태 토글
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Schedules> toggleComplete(@PathVariable Long id) {
        Schedules updatedSchedule = scheduleService.toggleScheduleCompletion(id);
        return ResponseEntity.ok(updatedSchedule);
    }

    // 일정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }

}
