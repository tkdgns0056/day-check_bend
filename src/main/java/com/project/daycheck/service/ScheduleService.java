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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getAllSchedules() {

        // 2025.04.11 추가 - 사용자 id기준으로 데이터 가져옴.
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
     * 특정 날짜의 일정 조회 (일반 일정 + 완료 상태가 적용된 반복 일정)
     */
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getScheduleByDate(LocalDate date){
        Long memberId = getCurrentMemberId();

        // 변수 설정 해야함. 왜? 레파지톨에서 가져올거기 떄문에 변수를 할당 받아야함.
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);

        List<Schedules> schedules = scheduleRepository.findSchedulesForDateRangeAndMember(startOfDay, endOfDay, memberId);

        // stream 으로 dto -> entity 변환 시키면서 특정 날짜 일정 조회 된것을 List 형태로 리턴
        return schedules.stream()
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

        // 일정 정보 업데이트 -> Schedule 엔티티안에 ddd 형태로 비즈니스 로직을 만들어둬서 서비스로직에서 불러옴.
        schedules.updateContent(request.getContent());
        schedules.updateTimes(request.getStartDate(), request.getEndDate());
        schedules.updatePriority(request.getPriority());
        schedules.updateDescription(request.getDescription());

        // 토글 상태가 null이 아니고, 업데이트하는 토글과 클라이언트의 현재 토글이 다르면인데,,, 이 로직은 이미 일정이 완료된 토글 자체인 경우 수정 불가함으로 바꿔야할거같음.
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