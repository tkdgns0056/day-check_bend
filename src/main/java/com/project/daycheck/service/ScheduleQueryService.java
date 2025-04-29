package com.project.daycheck.service;

import com.project.daycheck.dto.CompletionHistoryDTO;
import com.project.daycheck.dto.ScheduleDTO;
import com.project.daycheck.entity.Member;
import com.project.daycheck.exception.BusinessException;
import com.project.daycheck.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 일정 조회 통합 서비스
 * 일반 일정과 반복 일정을 함께 조회하고 완료 상태를 적용하는 역할
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleQueryService {

    private final ScheduleService scheduleService;
    private final RecurringScheduleService recurringScheduleService;
    private final CompletionHistoryService completionHistoryService;
    private final MemberService memberService;

    /**
     * 현재 인증된 사용자의 ID를 가져옴
     */
    private Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        Member member = memberService.findMemberByEmail(authentication.getName());
        return member.getId();
    }

    /**
     * 특정 날짜의 모든 일정 조회 (일반 + 반복)
     */
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getAllSchedulesByDate(LocalDate date) {
        // 1. 일반 일정 조회
        List<ScheduleDTO> regularSchedules = scheduleService.getScheduleByDate(date);

        // 2. 반복 일정 조회
        List<ScheduleDTO> recurringSchedules = recurringScheduleService.getRecurringSchedulesByDate(date);

        // 3. 완료 상태 조회 및 맵 생성
        List<CompletionHistoryDTO> completions = completionHistoryService.getCompletionByDate(date);
        Map<String, Boolean> completionMap = completions.stream()
                .collect(Collectors.toMap(
                        c -> (c.getIsRecurring() ? "R" : "S") + c.getScheduleId(),
                        CompletionHistoryDTO::getCompleted
                ));

        // 4. 완료 상태 적용
        applyCompletionStatus(regularSchedules, completionMap, false);
        applyCompletionStatus(recurringSchedules, completionMap, true);

        // 5. 모든 일정 합치기
        List<ScheduleDTO> allSchedules = new ArrayList<>();
        allSchedules.addAll(regularSchedules);
        allSchedules.addAll(recurringSchedules);

        // 6. 정렬 및 반환
        return sortSchedules(allSchedules);
    }

    /**
     * 완료 상태 적용 헬퍼 메소드
     */
    private void applyCompletionStatus(List<ScheduleDTO> schedules, Map<String, Boolean> completionMap, boolean isRecurring) {
        for (int i = 0; i < schedules.size(); i++) {
            ScheduleDTO schedule = schedules.get(i);
            String key = (isRecurring ? "R" : "S") + schedule.getId();

            if (completionMap.containsKey(key)) {
                // 기존 DTO 객체의 값을 유지하면서 completed 속성만 업데이트한 새 객체 생성
                ScheduleDTO updatedSchedule = ScheduleDTO.builder()
                        .id(schedule.getId())
                        .content(schedule.getContent())
                        .startDate(schedule.getStartDate())
                        .endDate(schedule.getEndDate())
                        .completed(completionMap.get(key)) // 완료 상태 업데이트
                        .priority(schedule.getPriority())
                        .description(schedule.getDescription())
                        .createdAt(schedule.getCreatedAt())
                        .updateAt(schedule.getUpdateAt())
                        .build();

                // 리스트의 해당 위치에 새 객체로 교체
                schedules.set(i, updatedSchedule);
            }
        }
    }

    /**
     * 일정 정렬 헬퍼 메소드
     * 우선순위와 시간 기준으로 정렬
     */
    private List<ScheduleDTO> sortSchedules(List<ScheduleDTO> schedules) {
        return schedules.stream()
                .sorted(Comparator
                        .comparing((ScheduleDTO s) -> {
                            // 우선 우선순위로 정렬 (high > medium > low)
                            if ("high".equals(s.getPriority())) return 0;
                            if ("medium".equals(s.getPriority())) return 1;
                            if ("low".equals(s.getPriority())) return 2;
                            return 3; // 우선순위가 없는 경우
                        })
                        .thenComparing(ScheduleDTO::getStartDate) // 그 다음 시작 시간 순
                )
                .collect(Collectors.toList());
    }
}