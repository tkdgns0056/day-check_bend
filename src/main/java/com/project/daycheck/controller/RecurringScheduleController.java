package com.project.daycheck.controller;

import com.project.daycheck.dto.RecurringGroupDTO;
import com.project.daycheck.dto.request.RecurringScheduleRequest;
import com.project.daycheck.dto.request.RecurringScheduleUpdateRequest;
import com.project.daycheck.entity.Schedules;
import com.project.daycheck.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules/recurring")
@RequiredArgsConstructor
public class RecurringScheduleController {

    private final ScheduleService scheduleService;

    // 모든 반복 일정 그룹 조회
    @GetMapping("/groups")
    public ResponseEntity<List<RecurringGroupDTO>> getAllRecurringGroups() {
        List<RecurringGroupDTO> recurringGroups = scheduleService.getAllRecurringGroups();
        return ResponseEntity.ok(recurringGroups);
    }

    // 특정 반복 일정 그룹 조회
    @GetMapping("/{parentId}")
    public ResponseEntity<List<Schedules>> getRecurringScheduleGroup(@PathVariable Long parentId) {
        List<Schedules> schedules = scheduleService.getRecurringSchedulesByParentId(parentId);
        return ResponseEntity.ok(schedules);
    }

    // 반복 일정 추가
    @PostMapping
    public ResponseEntity<List<Schedules>> addRecurringSchedule(@RequestBody RecurringScheduleRequest request) {
        List<Schedules> createdSchedules = scheduleService.addRecurringSchedule(request);
        return ResponseEntity.ok(createdSchedules);
    }

    // 반복 일정 수정 (PUT 메서드)
    @PutMapping("/{parentId}")
    public ResponseEntity<List<Schedules>> updateRecurringSchedules(
            @PathVariable Long parentId,
            @RequestBody RecurringScheduleUpdateRequest request) {
        List<Schedules> updatedSchedules = scheduleService.updateRecurringSchedules(
                parentId,
                request.getContent(),
                request.getPriority(),
                request.getRecurrencePattern(),
                request.getStartDate(),
                request.getEndDate(),
                request.getDescription()
        );
        return ResponseEntity.ok(updatedSchedules);
    }

    // 반복 일정 부분 수정 (PATCH 메서드)
    @PatchMapping("/{parentId}")
    public ResponseEntity<List<Schedules>> patchRecurringSchedules(
            @PathVariable Long parentId,
            @RequestBody Map<String, Object> updates) {
        List<Schedules> updatedSchedules = scheduleService.patchRecurringSchedules(parentId, updates);
        return ResponseEntity.ok(updatedSchedules);
    }

    // 반복 일정 일괄 삭제
    @DeleteMapping("/{parentId}")
    public ResponseEntity<Void> deleteRecurringSchedules(@PathVariable Long parentId) {
        scheduleService.deleteRecurringSchedules(parentId);
        return ResponseEntity.ok().build();
    }

}
