package com.project.daycheck.service;

import com.project.daycheck.dto.RecurringGroupDTO;
import com.project.daycheck.dto.ScheduleDTO;
import com.project.daycheck.dto.request.RecurringScheduleRequest;
import com.project.daycheck.entity.Member;
import com.project.daycheck.entity.Schedules;
import com.project.daycheck.exception.BusinessException;
import com.project.daycheck.exception.ErrorCode;
import com.project.daycheck.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberService memberService;

    /**
     * 현재 인증된 사용자의 ID를 가져옴.
     * 2025.04.11 추가 - 사용자 id기준으로 데이터 가져옴.
     */
    private Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        Member member = memberService.findMemberByEmail(authentication.getName());
        return member.getId();
    }

    /**
     * 현재 사용자의 모든 일정을 조회합니다.
     */
    public List<Schedules> getAllSchedules() {
        // 2025.04.11 추가 - 사용자 id기준으로 데이터 가져옴.
        Long memberId = getCurrentMemberId();
        return scheduleRepository.findByMemberId(memberId);
    }

    /**
     * 현재 사용자의 특정 날짜의 일정을 조회합니다.
     */
    public List<Schedules> getSchedulesByDate(LocalDate date) {
        // 2025.04.11 추가 - 사용자 id기준으로 데이터 가져옴.
        Long memberId = getCurrentMemberId();

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);

        return scheduleRepository.findSchedulesForDateRangeAndMember(
                startOfDay, endOfDay, memberId);
    }

    /**
     * 현재 사용자의 일정 내용을 수정합니다.
     */
    @Transactional
    public Schedules updateSchedule(Long id, ScheduleDTO request) {
        // 2025.04.11 추가 - 사용자 id기준으로 데이터 확인
        Long memberId = getCurrentMemberId();
        Schedules schedule = scheduleRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 요청에 포함된 필드만 업데이트
        if (request.getContent() != null) {
            schedule.setContent(request.getContent());
        }

        if (request.getStartDate() != null) {
            schedule.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            schedule.setEndDate(request.getEndDate());
        }

        if (request.getPriority() != null) {
            schedule.setPriority(request.getPriority());
        }

        if (request.getDescription() != null) {
            schedule.setDescription(request.getDescription());
        }

        if (request.getCompleted() != null) {
            schedule.setCompleted(request.getCompleted());
        }

        return scheduleRepository.save(schedule);
    }

    /**
     * 일정의 우선순위를 수정합니다.
     */
    @Transactional
    public Schedules updateSchedulePriority(Long id, String priority) {
        // 2025.04.11 추가 - 사용자 id기준으로 데이터 확인
        Long memberId = getCurrentMemberId();
        Schedules schedule = scheduleRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        schedule.setPriority(priority);
        return scheduleRepository.save(schedule);
    }

    /**
     * 새 일정을 추가
     */
    @Transactional
    public Schedules addSchedule(ScheduleDTO request) {
        Long memberId = getCurrentMemberId();

        Schedules schedule = Schedules.builder()
                .content(request.getContent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .priority(request.getPriority())
                .description(request.getDescription())
                .completed(request.getCompleted() != null ? request.getCompleted() : false)
                .memberId(memberId)
                .build();

        return scheduleRepository.save(schedule);
    }

    /**
     * 일정을 수정합니다.
     */
    @Transactional
    public Schedules updateSchedule(Long id, Schedules updatedSchedule) {
        // 2025.04.11 추가 - 사용자 id기준으로 데이터 확인
        Long memberId = getCurrentMemberId();
        Schedules existingSchedule = scheduleRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        existingSchedule.setContent(updatedSchedule.getContent());
        existingSchedule.setStartDate(updatedSchedule.getStartDate());
        existingSchedule.setEndDate(updatedSchedule.getEndDate());
        existingSchedule.setCompleted(updatedSchedule.getCompleted());
        existingSchedule.setPriority(updatedSchedule.getPriority());
        existingSchedule.setDescription(updatedSchedule.getDescription());

        return scheduleRepository.save(existingSchedule);
    }

    /**
     * 일정을 삭제합니다.
     */
    @Transactional
    public void deleteSchedule(Long id) {
        Long memberId = getCurrentMemberId();
        // 본인의 일정만 삭제 가능하도록 검증
        Schedules schedules = scheduleRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        scheduleRepository.delete(schedules);
    }

    /**
     * 일정의 완료 상태를 토글
     */
    @Transactional
    public Schedules toggleScheduleCompletion(Long id) {
        Long memberId = getCurrentMemberId();
        Schedules schedule = scheduleRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        schedule.setCompleted(!schedule.getCompleted());
        return scheduleRepository.save(schedule);
    }

    /**
     * 모든 반복 일정 그룹을 조회
     */
    public List<RecurringGroupDTO> getAllRecurringGroups() {
        Long memberId = getCurrentMemberId();

        // 현재 사용자의 부모 일정을 모두 조회
        List<Schedules> parentSchedules = scheduleRepository.findByMemberIdAndParentScheduleIdIsNull(memberId)
                .stream()
                .filter(s -> s.getRecurrencePattern() != null && !s.getRecurrencePattern().isEmpty())
                .collect(Collectors.toList());

        List<RecurringGroupDTO> groups = new ArrayList<>();

        for (Schedules parent : parentSchedules) {
            // 각 부모 일정에 속한 하위 일정 조회
            List<Schedules> childSchedules = scheduleRepository.findByParentScheduleIdAndMemberId(parent.getId(), memberId);

            if (!childSchedules.isEmpty()) {
                // 그룹 정보 생성
                RecurringGroupDTO group = new RecurringGroupDTO();
                group.setParentSchedule(parent);

                // 시작일과 종료일 계산
                LocalDate startDate = childSchedules.stream()
                        .map(s -> s.getStartDate().toLocalDate())
                        .min(LocalDate::compareTo)
                        .orElse(null);

                LocalDate endDate = childSchedules.stream()
                        .map(s -> s.getEndDate().toLocalDate())
                        .max(LocalDate::compareTo)
                        .orElse(null);

                // 날짜 포맷팅
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                group.setStartDate(startDate != null ? startDate.format(formatter) : "");
                group.setEndDate(endDate != null ? endDate.format(formatter) : "");

                // 하위 일정 개수
                group.setScheduleCount(childSchedules.size());

                groups.add(group);
            }
        }

        return groups;
    }

    /**
     * 부모 ID로 반복 일정 그룹을 조회합니다.
     */
    public List<Schedules> getRecurringSchedulesByParentId(Long parentId) {
        // 2025.04.11 추가 - 사용자 id기준으로 데이터 확인
        Long memberId = getCurrentMemberId();

        // 부모 일정 조회
        Schedules parentSchedule = scheduleRepository.findByIdAndMemberId(parentId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 하위 일정 조회
        List<Schedules> childSchedules = scheduleRepository.findByParentScheduleIdAndMemberId(parentId, memberId);

        // 부모 일정과 하위 일정 합치기
        List<Schedules> allSchedules = new ArrayList<>();
        allSchedules.add(parentSchedule);
        allSchedules.addAll(childSchedules);

        return allSchedules;
    }

    /**
     * 반복 일정을 추가합니다.
     */
    @Transactional
    public List<Schedules> addRecurringSchedule(RecurringScheduleRequest request) {
        Long memberId = getCurrentMemberId();

        // 부모 일정 생성
        Schedules parentSchedule = Schedules.builder()
                .content(request.getContent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .completed(false)
                .recurrencePattern(request.getRecurrencePattern())
                .priority(request.getPriority())
                .description(request.getDescription())
                .memberId(memberId)
                .build();

        parentSchedule = scheduleRepository.save(parentSchedule);

        List<Schedules> allSchedules = new ArrayList<>();
        allSchedules.add(parentSchedule);

        // 반복 패턴에 따라 하위 일정 생성
        List<Schedules> childSchedules = generateChildSchedules(
                parentSchedule,
                request.getStartDate(),
                request.getEndDate(),
                request.getRecurrencePattern(),
                memberId
        );

        if (!childSchedules.isEmpty()) {
            scheduleRepository.saveAll(childSchedules);
            allSchedules.addAll(childSchedules);
        }

        return allSchedules;
    }

    /**
     * 반복 일정을 업데이트합니다.
     */
    @Transactional
    public List<Schedules> updateRecurringSchedules(
            Long parentId,
            String content,
            String priority,
            String recurrencePattern,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String description
    ) {
        // 2025.04.11 추가 - 사용자 id기준으로 데이터 확인
        Long memberId = getCurrentMemberId();

        // 부모 일정 조회
        Schedules parentSchedule = scheduleRepository.findByIdAndMemberId(parentId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 기존 하위 일정 조회
        List<Schedules> existingChildSchedules = scheduleRepository.findByParentScheduleIdAndMemberId(parentId, memberId);

        // 시작일과 종료일이 변경되었는지 확인
        boolean datesChanged = !parentSchedule.getStartDate().equals(startDate) ||
                !parentSchedule.getEndDate().equals(endDate) ||
                !parentSchedule.getRecurrencePattern().equals(recurrencePattern);

        // 부모 일정 업데이트
        parentSchedule.setContent(content);
        parentSchedule.setPriority(priority);
        parentSchedule.setRecurrencePattern(recurrencePattern);
        parentSchedule.setStartDate(startDate);
        parentSchedule.setEndDate(endDate);
        parentSchedule.setDescription(description);

        scheduleRepository.save(parentSchedule);

        // 날짜나 반복 패턴이 변경된 경우 하위 일정 재생성
        if (datesChanged) {
            // 기존 하위 일정 삭제
            if (!existingChildSchedules.isEmpty()) {
                scheduleRepository.deleteAll(existingChildSchedules);
            }

            // 새 하위 일정 생성
            List<Schedules> newChildSchedules = generateChildSchedules(
                    parentSchedule, startDate, endDate, recurrencePattern, memberId);

            if (!newChildSchedules.isEmpty()) {
                scheduleRepository.saveAll(newChildSchedules);
            }

            // 모든 일정 반환
            List<Schedules> allSchedules = new ArrayList<>();
            allSchedules.add(parentSchedule);
            allSchedules.addAll(newChildSchedules);
            return allSchedules;
        } else {
            // 날짜나 반복 패턴이 변경되지 않은 경우, 하위 일정 내용만 업데이트
            for (Schedules child : existingChildSchedules) {
                child.setContent(content);
                child.setPriority(priority);
                // 설명은 부모 일정에만 저장하고 하위 일정에는 복사하지 않음
            }

            if (!existingChildSchedules.isEmpty()) {
                scheduleRepository.saveAll(existingChildSchedules);
            }

            // 모든 일정 반환
            List<Schedules> allSchedules = new ArrayList<>();
            allSchedules.add(parentSchedule);
            allSchedules.addAll(existingChildSchedules);
            return allSchedules;
        }
    }

    /**
     * 반복 일정을 부분적으로 업데이트합니다.
     */
    @Transactional
    public List<Schedules> patchRecurringSchedules(Long parentId, Map<String, Object> updates) {
        // 2025.04.11 추가 - 사용자 id기준으로 데이터 확인
        Long memberId = getCurrentMemberId();

        // 부모 일정 조회
        Schedules parentSchedule = scheduleRepository.findByIdAndMemberId(parentId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 업데이트할 필드 적용
        if (updates.containsKey("content")) {
            parentSchedule.setContent((String) updates.get("content"));
        }

        if (updates.containsKey("description")) {
            parentSchedule.setDescription((String) updates.get("description"));
        }

        if (updates.containsKey("priority")) {
            parentSchedule.setPriority((String) updates.get("priority"));
        }

        // 부모 일정 저장
        scheduleRepository.save(parentSchedule);

        // 모든 하위 일정 조회
        List<Schedules> childSchedules = scheduleRepository.findByParentScheduleIdAndMemberId(parentId, memberId);

        // 하위 일정들도 업데이트
        if (!childSchedules.isEmpty()) {
            for (Schedules child : childSchedules) {
                if (updates.containsKey("content")) {
                    child.setContent(parentSchedule.getContent());
                }

                if (updates.containsKey("priority")) {
                    child.setPriority(parentSchedule.getPriority());
                }

                // 설명은 부모 일정에만 저장하고 하위 일정에는 복사하지 않음
            }

            // 하위 일정 저장
            scheduleRepository.saveAll(childSchedules);
        }

        // 부모 일정과 하위 일정을 합친 목록 반환
        List<Schedules> allSchedules = new ArrayList<>();
        allSchedules.add(parentSchedule);
        allSchedules.addAll(childSchedules);
        return allSchedules;
    }

    /**
     * 반복 일정과 모든 하위 일정을 삭제합니다.
     */
    @Transactional
    public void deleteRecurringSchedules(Long parentId) {
        // 2025.04.11 추가 - 사용자 id기준으로 데이터 확인
        Long memberId = getCurrentMemberId();

        // 부모 일정 확인
        Schedules parentSchedule = scheduleRepository.findByIdAndMemberId(parentId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 하위 일정 조회 및 알림 삭제
        List<Schedules> childSchedules = scheduleRepository.findByParentScheduleIdAndMemberId(parentId, memberId);
        if (!childSchedules.isEmpty()) {
            // 하위 일정 삭제
            scheduleRepository.deleteAll(childSchedules);
        }
        // 부모 일정 삭제
        scheduleRepository.delete(parentSchedule);
    }

    /**
     * 반복 일정의 모든 하위 일정 완료 상태를 설정합니다.
     */
    @Transactional
    public List<Schedules> markAllRecurringSchedules(Long parentId, boolean completed) {
        // 2025.04.11 추가 - 사용자 id기준으로 데이터 확인
        Long memberId = getCurrentMemberId();

        // 부모 일정 확인
        scheduleRepository.findByIdAndMemberId(parentId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 하위 일정 조회
        List<Schedules> childSchedules = scheduleRepository.findByParentScheduleIdAndMemberId(parentId, memberId);

        // 하위 일정 완료 상태 설정
        for (Schedules child : childSchedules) {
            child.setCompleted(completed);
        }

        // 저장 및 반환
        return scheduleRepository.saveAll(childSchedules);
    }

    /**
     * 반복 패턴에 따라 하위 일정을 생성합니다.
     */
    private List<Schedules> generateChildSchedules(
            Schedules parentSchedule,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String recurrencePattern,
            Long memberId
    ) {
        List<Schedules> childSchedules = new ArrayList<>();
        LocalDate start = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();

        // 시작 시간과 종료 시간
        int startHour = startDate.getHour();
        int startMinute = startDate.getMinute();
        int endHour = endDate.getHour();
        int endMinute = endDate.getMinute();

        switch (recurrencePattern) {
            case "DAILY":
                // 매일 반복
                for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                    // 부모 일정과 같은 날짜는 건너뜀
                    if (date.equals(start)) {
                        continue;
                    }

                    Schedules child = createChildSchedule(
                            parentSchedule,
                            date,
                            startHour,
                            startMinute,
                            endHour,
                            endMinute,
                            memberId
                    );
                    childSchedules.add(child);
                }
                break;

            case "WEEKLY":
                // 매주 같은 요일에 반복
                DayOfWeek dayOfWeek = start.getDayOfWeek();
                for (LocalDate date = start.plusWeeks(1); !date.isAfter(end); date = date.plusWeeks(1)) {
                    Schedules child = createChildSchedule(
                            parentSchedule,
                            date,
                            startHour,
                            startMinute,
                            endHour,
                            endMinute,
                            memberId
                    );
                    childSchedules.add(child);
                }
                break;

            case "WEEKDAY":
                // 평일(월~금)에만 반복
                for (LocalDate date = start.plusDays(1); !date.isAfter(end); date = date.plusDays(1)) {
                    DayOfWeek day = date.getDayOfWeek();
                    if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                        Schedules child = createChildSchedule(
                                parentSchedule,
                                date,
                                startHour,
                                startMinute,
                                endHour,
                                endMinute,
                                memberId
                        );
                        childSchedules.add(child);
                    }
                }
                break;
        }

        return childSchedules;
    }

    /**
     * 하위 일정 객체를 생성합니다.
     */
    private Schedules createChildSchedule(
            Schedules parentSchedule,
            LocalDate date,
            int startHour,
            int startMinute,
            int endHour,
            int endMinute,
            Long memberId
    ) {
        LocalDateTime childStartDate = date.atTime(startHour, startMinute);
        LocalDateTime childEndDate = date.atTime(endHour, endMinute);

        return Schedules.builder()
                .content(parentSchedule.getContent())
                .startDate(childStartDate)
                .endDate(childEndDate)
                .completed(false)
                .parentScheduleId(parentSchedule.getId())
                .priority(parentSchedule.getPriority())
                .memberId(memberId)
                .build();
    }
}