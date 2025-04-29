package com.project.daycheck.service;

import com.project.daycheck.dto.RecurringExceptionDTO;
import com.project.daycheck.dto.RecurringScheduleDTO;
import com.project.daycheck.dto.ScheduleDTO;
import com.project.daycheck.dto.request.RecurringScheduleRequest;
import com.project.daycheck.entity.Member;
import com.project.daycheck.entity.RecurringException;
import com.project.daycheck.entity.RecurringSchedule;
import com.project.daycheck.entity.RecurringScheduleDay;
import com.project.daycheck.exception.BusinessException;
import com.project.daycheck.exception.ErrorCode;
import com.project.daycheck.repository.RecurringExceptionRepository;
import com.project.daycheck.repository.RecurringScheduleDayRepository;
import com.project.daycheck.repository.RecurringScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 반복 일정 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecurringScheduleService {

    private final RecurringScheduleRepository recurringScheduleRepository;
    private final RecurringScheduleDayRepository recurringScheduleDayRepository;
    private final RecurringExceptionRepository recurringExceptionRepository;
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
     * 모든 반복 일정 조회
     */
    @Transactional(readOnly = true)
    public List<RecurringScheduleDTO> getAllRecurringSchedules() {
        Long memberId = getCurrentMemberId();
        List<RecurringSchedule> recurringSchedules = recurringScheduleRepository.findByMemberId(memberId);

        return recurringSchedules.stream()
                .map(RecurringScheduleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 ID의 반복 일정 조회
     */
    @Transactional(readOnly = true)
    public RecurringScheduleDTO getRecurringScheduleById(Long recurringScheduleId) {
        Long memberId = getCurrentMemberId();
        RecurringSchedule recurringSchedule = recurringScheduleRepository.findByIdAndMemberId(recurringScheduleId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        return RecurringScheduleDTO.fromEntity(recurringSchedule);
    }

    /**
     * 특정 날짜의 반복 일정 조회
     */
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getRecurringSchedulesByDate(LocalDate date) {
        Long memberId = getCurrentMemberId();

        // 해당 날짜의 활성화된 모든 반복 일정 패턴 조회
        List<RecurringSchedule> activePatterns = recurringScheduleRepository.findActiveOnDate(memberId, date.atStartOfDay());

        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();

        // 각 패턴에 대해 해당 날짜가 패턴에 해당하는지 확인하고 일정 생성
        for(RecurringSchedule pattern : activePatterns) {
            if(isDateMatchingPattern(date, pattern)){
                // 예외 확인
                Optional<RecurringException> exceptionOpt = recurringExceptionRepository.findByRecurringScheduleIdAndExceptionDate(pattern.getId(), date);

                // 건너뛰기 예외가 있으면 스킵
                if(exceptionOpt.isPresent() && "SKIP".equals(exceptionOpt.get().getExceptionType())){
                    continue;
                }

                // 일정 정보 생성
                ScheduleDTO scheduleDTO = createScheduleFromPattern(pattern, date, exceptionOpt.orElse(null));
                scheduleDTOS.add(scheduleDTO);
            }
        }

        return scheduleDTOS;
    }

    /**
     * 패턴과 날짜로부터 일정 DTO 생성
     */
    private ScheduleDTO createScheduleFromPattern(RecurringSchedule pattern, LocalDate date, RecurringException exception) {
        // 시작 시간과 종료 시간 파싱
        LocalTime startTime = LocalTime.parse(pattern.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTime = LocalTime.parse(pattern.getEndTime(), DateTimeFormatter.ofPattern("HH:mm"));

        // 날짜와 시간 결합
        LocalDateTime startDateTime = date.atTime(startTime);
        LocalDateTime endDateTime = date.atTime(endTime);

        String title = pattern.getContent();
        String priority = pattern.getPriority();
        String description = pattern.getDescription();

        // 수정 예외가 있으면 적용
        if(exception != null && "MODIFY".equals(exception.getExceptionType())) {
            if(exception.getModifiedTitle() != null) {
                title = exception.getModifiedTitle();
            }

            if(exception.getModifiedStartTime() != null) {
                startDateTime = date.atTime(LocalTime.parse(exception.getModifiedStartTime(), DateTimeFormatter.ofPattern("HH:mm")));
            }
            if (exception.getModifiedEndTime() != null) {
                endDateTime = date.atTime(LocalTime.parse(exception.getModifiedEndTime(), DateTimeFormatter.ofPattern("HH:mm")));
            }

            if (exception.getModifiedPriority() != null) {
                priority = exception.getModifiedPriority();
            }

            if (exception.getModifiedDescription() != null) {
                description = exception.getModifiedDescription();
            }
        }

        // 가상 일정 DTO 생성(실제 DB 저장 X)
        return ScheduleDTO.builder()
                .id(pattern.getId() * -1) // 음수 ID로 반복 일정 구분
                .content(title)
                .startDate(startDateTime)
                .endDate(endDateTime)
                .completed(false) // 반복 일정은 기본적으로 완료되지 않은 상태
                .priority(priority)
                .description(description)
                .build();
    }

    /**
     * 날짜가 패턴과 일치하는지 확인
     */
    private boolean isDateMatchingPattern(LocalDate date, RecurringSchedule pattern) {
        // 패턴의 시작일과 종료일 범위 확인
        LocalDate patternStartDate = pattern.getStartDate().toLocalDate();
        LocalDate patternEndDate = pattern.getEndDate() != null ? pattern.getEndDate().toLocalDate() : LocalDate.now().plusYears(100);

        if (date.isBefore(patternStartDate) || date.isAfter(patternEndDate)) {
            return false;
        }

        switch (pattern.getPatternType()) {
            case "DAILY":
                // 매일 반복 - 간격 고려
                long daysBetween = ChronoUnit.DAYS.between(patternStartDate, date);
                return daysBetween % pattern.getInterval() == 0;

            case "WEEKLY":
                // 매주 특정 요일 반복
                if (!pattern.getScheduleDays().isEmpty()) {
                    // 요일 목록에 포함되는지 확인
                    DayOfWeek currentDayOfWeek = date.getDayOfWeek();
                    boolean isDayMatching = pattern.containsDay(currentDayOfWeek);

                    if (!isDayMatching) {
                        return false;
                    }

                    // 주 간격 계산
                    LocalDate firstDayOfWeek = patternStartDate
                            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    LocalDate dateDayOfWeek = date
                            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

                    long weeksBetween = ChronoUnit.WEEKS.between(firstDayOfWeek, dateDayOfWeek);

                    return weeksBetween % pattern.getInterval() == 0;
                } else {
                    // 시작일과 같은 요일만 해당
                    boolean isSameDayOfWeek = date.getDayOfWeek() == patternStartDate.getDayOfWeek();
                    long weeksBetween = ChronoUnit.WEEKS.between(
                            patternStartDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                            date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));

                    return isSameDayOfWeek && weeksBetween % pattern.getInterval() == 0;
                }

            case "MONTHLY":
                // 매월 특정 일 또는 특정 주차 특정 요일
                if (pattern.getDayOfMonth() != null) {
                    // 매월 n일
                    boolean isSameDayOfMonth = date.getDayOfMonth() == pattern.getDayOfMonth();
                    long monthsBetween = ChronoUnit.MONTHS.between(
                            patternStartDate.withDayOfMonth(1),
                            date.withDayOfMonth(1));

                    return isSameDayOfMonth && monthsBetween % pattern.getInterval() == 0;
                } else if (pattern.getWeekOfMonth() != null && !pattern.getScheduleDays().isEmpty()) {
                    // 패턴에 저장된 첫 번째 요일 가져오기 (예: "매월 n번째 월요일")
                    DayOfWeek dayOfWeek = pattern.getScheduleDays().get(0).getDayOfWeek();
                    int weekOfMonth = (date.getDayOfMonth() - 1) / 7 + 1; // 월의 몇 번째 주인지 계산

                    boolean isMatchingWeekAndDay = weekOfMonth == pattern.getWeekOfMonth()
                            && date.getDayOfWeek() == dayOfWeek;

                    long monthsBetween = ChronoUnit.MONTHS.between(
                            patternStartDate.withDayOfMonth(1),
                            date.withDayOfMonth(1));

                    return isMatchingWeekAndDay && monthsBetween % pattern.getInterval() == 0;
                }
                return false;

            case "YEARLY":
                // 매년 같은 날짜
                boolean isSameMonthAndDay = date.getMonthValue() == patternStartDate.getMonthValue()
                        && date.getDayOfMonth() == patternStartDate.getDayOfMonth();

                long yearsBetween = ChronoUnit.YEARS.between(patternStartDate, date);

                return isSameMonthAndDay && yearsBetween % pattern.getInterval() == 0;

            case "CUSTOM":
                // 사용자 정의 패턴 (요일 지정)
                if (!pattern.getScheduleDays().isEmpty()) {
                    DayOfWeek currentDayOfWeek = date.getDayOfWeek();
                    return pattern.containsDay(currentDayOfWeek);
                }
                return false;

            default:
                return false;
        }
    }

    /**
     * 새 반복 일정 추가
     */
    @Transactional
    public RecurringScheduleDTO createRecurringSchedule(RecurringScheduleRequest request) {
        Long memberId = getCurrentMemberId();

        // 시작/종료 시간 설정
        String startTime = request.getStartTime();
        String endTime = request.getEndTime();

        if (startTime == null) {
            startTime = request.getStartDate().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        if (endTime == null) {
            endTime = request.getEndDate().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        // 반복 일정 생성
        RecurringSchedule recurringSchedule = RecurringSchedule.builder()
                .memberId(memberId)
                .content(request.getContent())
                .patternType(request.getPatternType())
                .interval(request.getInterval() != null ? request.getInterval() : 1)
                .dayOfMonth(request.getDayOfMonth())
                .weekOfMonth(request.getWeekOfMonth())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(startTime)
                .endTime(endTime)
                .priority(request.getPriority())
                .description(request.getDescription())
                .scheduleDays(new ArrayList<>())
                .build();

        // 저장하여 ID 생성
        RecurringSchedule savedSchedule = recurringScheduleRepository.save(recurringSchedule);

        // 요일 정보 추가
        if (request.getDaysOfWeek() != null && !request.getDaysOfWeek().isEmpty()) {
            for (DayOfWeek day : request.getDaysOfWeek()) {
                RecurringScheduleDay scheduleDay = RecurringScheduleDay.builder()
                        .recurringScheduleId(savedSchedule.getId())
                        .dayOfWeek(day)
                        .build();
                scheduleDay.setRecurringSchedule(savedSchedule);
                savedSchedule.getScheduleDays().add(scheduleDay);
                recurringScheduleDayRepository.save(scheduleDay);
            }
        }

        return RecurringScheduleDTO.fromEntity(savedSchedule);
    }

    /**
     * 반복 일정 수정
     */
    @Transactional
    public RecurringScheduleDTO updateRecurringSchedule(Long recurringScheduleId, RecurringScheduleRequest request) {
        Long memberId = getCurrentMemberId();

        // 반복 일정 조회
        RecurringSchedule recurringSchedule = recurringScheduleRepository.findByIdAndMemberId(recurringScheduleId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 필드 업데이트
        recurringSchedule.updateContent(request.getContent());

        if (request.getPatternType() != null && !request.getPatternType().equals(recurringSchedule.getPatternType())) {
            // 패턴 타입이 변경되었을 경우, 관련 필드들도 함께 업데이트
            RecurringSchedule updatedSchedule = RecurringSchedule.builder()
                    .id(recurringSchedule.getId())
                    .memberId(memberId)
                    .content(request.getContent() != null ? request.getContent() : recurringSchedule.getContent())
                    .patternType(request.getPatternType())
                    .interval(request.getInterval() != null ? request.getInterval() : recurringSchedule.getInterval())
                    .dayOfMonth(request.getDayOfMonth())
                    .weekOfMonth(request.getWeekOfMonth())
                    .startDate(request.getStartDate() != null ? request.getStartDate() : recurringSchedule.getStartDate())
                    .endDate(request.getEndDate() != null ? request.getEndDate() : recurringSchedule.getEndDate())
                    .startTime(request.getStartTime() != null ? request.getStartTime() : recurringSchedule.getStartTime())
                    .endTime(request.getEndTime() != null ? request.getEndTime() : recurringSchedule.getEndTime())
                    .priority(request.getPriority() != null ? request.getPriority() : recurringSchedule.getPriority())
                    .description(request.getDescription() != null ? request.getDescription() : recurringSchedule.getDescription())
                    .scheduleDays(new ArrayList<>())
                    .build();

            // 기존 요일 정보 삭제
            recurringScheduleDayRepository.deleteByRecurringScheduleId(recurringScheduleId);

            // 새 요일 정보 추가
            if (request.getDaysOfWeek() != null && !request.getDaysOfWeek().isEmpty()) {
                for (DayOfWeek day : request.getDaysOfWeek()) {
                    RecurringScheduleDay scheduleDay = RecurringScheduleDay.builder()
                            .recurringScheduleId(updatedSchedule.getId())
                            .dayOfWeek(day)
                            .build();
                    scheduleDay.setRecurringSchedule(updatedSchedule);
                    updatedSchedule.getScheduleDays().add(scheduleDay);
                    recurringScheduleDayRepository.save(scheduleDay);
                }
            }

            // 데이터 복사
            for (RecurringException exception : recurringSchedule.getExceptions()) {
                updatedSchedule.addException(exception);
            }

            recurringSchedule = updatedSchedule;
        } else {
            // 개별 필드 업데이트
            if (request.getInterval() != null) {
                recurringSchedule.updateInterval(request.getInterval());
            }

            if (request.getDaysOfWeek() != null) {
                // 기존 요일 정보 삭제
                recurringScheduleDayRepository.deleteByRecurringScheduleId(recurringScheduleId);

                // 새 요일 정보 추가
                for (DayOfWeek day : request.getDaysOfWeek()) {
                    RecurringScheduleDay scheduleDay = RecurringScheduleDay.builder()
                            .recurringScheduleId(recurringSchedule.getId())
                            .dayOfWeek(day)
                            .build();
                    scheduleDay.setRecurringSchedule(recurringSchedule);
                    recurringSchedule.getScheduleDays().add(scheduleDay);
                    recurringScheduleDayRepository.save(scheduleDay);
                }
            }

            if (request.getDayOfMonth() != null) {
                recurringSchedule.updateDayOfMonth(request.getDayOfMonth());
            }

            if (request.getWeekOfMonth() != null) {
                recurringSchedule.updateWeekOfMonth(request.getWeekOfMonth());
            }

            if (request.getStartDate() != null || request.getEndDate() != null) {
                recurringSchedule.updateDateRange(request.getStartDate(), request.getEndDate());
            }

            if (request.getStartTime() != null || request.getEndTime() != null) {
                recurringSchedule.updateTimes(request.getStartTime(), request.getEndTime());
            }

            if (request.getPriority() != null) {
                recurringSchedule.updatePriority(request.getPriority());
            }

            if (request.getDescription() != null) {
                recurringSchedule.updateDescription(request.getDescription());
            }
        }

        RecurringSchedule updatedSchedule = recurringScheduleRepository.save(recurringSchedule);
        return RecurringScheduleDTO.fromEntity(updatedSchedule);
    }

    /**
     * 반복 일정 삭제
     */
    @Transactional
    public void deleteRecurringSchedule(Long recurringScheduleId) {
        Long memberId = getCurrentMemberId();

        // 반복 일정 조회
        RecurringSchedule recurringSchedule = recurringScheduleRepository.findByIdAndMemberId(recurringScheduleId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 요일 정보 삭제
        recurringScheduleDayRepository.deleteByRecurringScheduleId(recurringScheduleId);

        // 반복 일정 삭제 (예외 정보는 cascade로 함께 삭제됨)
        recurringScheduleRepository.delete(recurringSchedule);
    }

    /**
     * 기간별 일정 조회
     */
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getRecurringSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        Long memberId = getCurrentMemberId();
        List<ScheduleDTO> result = new ArrayList<>();

        // 범위 내 모든 날짜에 대해 반복 일정 조회
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<ScheduleDTO> dailySchedules = getRecurringSchedulesByDate(date);
            result.addAll(dailySchedules);
        }

        return result;
    }
}