package com.project.daycheck.controller;

import com.project.daycheck.dto.ScheduleDTO;
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
    @Operation(summary = "일정 조회", description = "로그인한 사용자의 특정 날짜 일정을 조회한다.")
    @GetMapping("/{date}")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ScheduleDTO> schedules = scheduleService.getScheduleByDate(date);
        return ResponseEntity.ok(schedules);
    }
}
