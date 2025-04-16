package com.project.daycheck.controller;

import com.project.daycheck.dto.RecurringScheduleDTO;
import com.project.daycheck.dto.request.RecurringScheduleRequest;
import com.project.daycheck.dto.request.RecurringScheduleUpdateRequest;
import com.project.daycheck.entity.Schedules;
import com.project.daycheck.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Tag(name = "반복 일정 관리 컨트롤러", description = "반복 일정 관리 API")
@RestController
@RequestMapping("/api/schedules/recurring")
@RequiredArgsConstructor
public class RecurringScheduleController {

    private final ScheduleService scheduleService;


}
