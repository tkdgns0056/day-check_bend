package com.project.daycheck.controller;

import com.project.daycheck.dto.RecurringScheduleDTO;
import com.project.daycheck.dto.request.RecurringScheduleRequest;
import com.project.daycheck.service.RecurringScheduleService;
import com.project.daycheck.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "반복 일정 관리 컨트롤러", description = "반복 일정 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/schedules/recurring")
@RequiredArgsConstructor
public class RecurringScheduleController {

    private final RecurringScheduleService scheduleService;

    @Operation(summary = "반복일정 조회", description = "사용자의 반복 일정을 조회한다.")
    @GetMapping
    public ResponseEntity<List<RecurringScheduleDTO>> getRecurringSchedules(){
       List<RecurringScheduleDTO> recurringScheduleDTOList = scheduleService.getAllRecurringSchedules();

       return ResponseEntity.ok(recurringScheduleDTOList);
    }

    @Operation(summary = "반복 일정 등록", description = "반복 일정을 등록한다")
    @PostMapping()
    public ResponseEntity<RecurringScheduleDTO> addRecurring(@Valid @RequestBody RecurringScheduleRequest scheduleRequest){
        log.info("반복 일정 등록{}", scheduleRequest);
        RecurringScheduleDTO scheduleDTO = scheduleService.createRecurringSchedule(scheduleRequest);

        return ResponseEntity.ok(scheduleDTO);
    }




}
