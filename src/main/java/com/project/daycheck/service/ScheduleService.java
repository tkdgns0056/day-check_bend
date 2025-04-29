package com.project.daycheck.service;

import com.project.daycheck.dto.ScheduleDTO;
import com.project.daycheck.dto.request.ScheduleRequest;
import com.project.daycheck.entity.Member;
import com.project.daycheck.entity.Schedules;
import com.project.daycheck.exception.BusinessException;
import com.project.daycheck.exception.ErrorCode;
import com.project.daycheck.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 일반 일정 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberService memberService;

    /**
     * 현재 인증된 사용자의 ID를 가져옴
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
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getAllSchedules() {
        Long memberId = getCurrentMemberId();
        List<Schedules> schedules = scheduleRepository.findByMemberId(memberId);

        return schedules.stream()
                .map(ScheduleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 ID의 일정 조회
     */
    @Transactional(readOnly = true)
    public ScheduleDTO getScheduleById(Long scheduleId){
        Long memberId = getCurrentMemberId();
        Schedules schedules = scheduleRepository.findByIdAndMemberId(scheduleId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        return ScheduleDTO.fromEntity(schedules);
    }

    /**
     * 특정 날짜의 일반 일정만 조회 (완료 상태 제외)
     */
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getScheduleByDate(LocalDate date){
        Long memberId = getCurrentMemberId();

        // 일반 일정만 조회
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);

        List<Schedules> regularSchedules = scheduleRepository.findSchedulesForDateRangeAndMember(
                startOfDay, endOfDay, memberId);

        // 엔티티를 DTO로 변환하여 반환
        return regularSchedules.stream()
                .map(ScheduleDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 새 일정 추가 (일반)
     */
    @Transactional
    public ScheduleDTO addSchedule(ScheduleRequest request){
        Long memberId = getCurrentMemberId();

        // 일정 생성
        Schedules schedules = Schedules.builder()
                .memberId(memberId)
                .content(request.getContent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .priority(request.getPriority())
                .description(request.getDescription())
                .completed(request.getCompleted() != null ? request.getCompleted() : false)
                .build();

        Schedules savedSchedule = scheduleRepository.save(schedules);
        return ScheduleDTO.fromEntity(savedSchedule);
    }

    /**
     * 일반 일정 수정
     */
    @Transactional
    public ScheduleDTO updateSchedule(Long scheduleId, ScheduleRequest request) {
        Long memberId = getCurrentMemberId();

        // 일정 조회
        Schedules schedules = scheduleRepository.findByIdAndMemberId(scheduleId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 일정 정보 업데이트
        schedules.updateContent(request.getContent());
        schedules.updateTimes(request.getStartDate(), request.getEndDate());
        schedules.updatePriority(request.getPriority());
        schedules.updateDescription(request.getDescription());

        // 완료 상태 업데이트
        if(request.getCompleted() != null && schedules.getCompleted() != request.getCompleted()){
            schedules.toggleComplete();
        }

        Schedules updatedSchedule = scheduleRepository.save(schedules);
        return ScheduleDTO.fromEntity(updatedSchedule);
    }

    /**
     * 일정 완료 상태 토글
     */
    @Transactional
    public ScheduleDTO toggleScheduleCompletion(Long scheduleId) {
        Long memberId = getCurrentMemberId();

        // 일정 조회
        Schedules schedules = scheduleRepository.findByIdAndMemberId(scheduleId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 완료 상태 토글
        schedules.toggleComplete();

        Schedules updatedSchedule = scheduleRepository.save(schedules);
        return ScheduleDTO.fromEntity(updatedSchedule);
    }

    /**
     * 일정 삭제
     */
    @Transactional
    public void deleteSchedules(Long scheduleId) {
        Long memberId = getCurrentMemberId();

        // 일정 존재 확인
        Schedules schedules = scheduleRepository.findByIdAndMemberId(scheduleId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 일정 삭제
        scheduleRepository.delete(schedules);
    }
}