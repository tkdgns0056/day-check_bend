package com.project.daycheck.controller;

import com.project.daycheck.dto.ScheduleDTO;
import com.project.daycheck.dto.request.ScheduleRequest;
import com.project.daycheck.service.ScheduleService;
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

@Tag(name = "일반 일정 관리 컨트롤러", description = "일반 일저 관리 API")
@Slf4j
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

    // 일정 등록
    @Operation(summary = "일정 등록", description = "로그인한 사용자의 일정을 등록한다.")
    @PostMapping
    public ResponseEntity<ScheduleDTO> addSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest){
         log.info("일정 등륵 {}", scheduleRequest);
         ScheduleDTO scheduleDTO = scheduleService.addSchedule(scheduleRequest);
         return ResponseEntity.ok(scheduleDTO);
    }

    @Operation(summary = "일정 토글", description = "로그인한 사용자의 일정 완료 여부를 입력한다")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ScheduleDTO> scheduleComplete(@PathVariable Long id){
        log.info("일정 토글 변경{}", id);
        ScheduleDTO scheduleDTO = scheduleService.toggleScheduleCompletion(id);
        return ResponseEntity.ok(scheduleDTO);
    }

    @Operation(summary = "일정 수정", description = "로그인한 사용자의 일정을 수정한다.")
    @PutMapping("{id}")
    public ResponseEntity<ScheduleDTO> scheduleUpdate(@PathVariable Long id, @RequestBody ScheduleRequest scheduleRequest){
        ScheduleDTO scheduleDTO = scheduleService.updateSchedule(id, scheduleRequest);
        return ResponseEntity.ok(scheduleDTO);
    }


    @Operation(summary = "일정 삭제", description = "로그인한 사용자의 일정을 삭제한다")
    @DeleteMapping("/{id}")
    public void scheduleDelete(@PathVariable Long id){
        log.info("일정 삭제{}", id);
        scheduleService.deleteSchedules(id);

    }
}
