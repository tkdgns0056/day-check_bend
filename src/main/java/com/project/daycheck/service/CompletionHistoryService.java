package com.project.daycheck.service;

import com.project.daycheck.dto.CompletionHistoryDTO;
import com.project.daycheck.entity.CompletionHistory;
import com.project.daycheck.entity.Member;
import com.project.daycheck.exception.BusinessException;
import com.project.daycheck.exception.ErrorCode;
import com.project.daycheck.repository.CompletionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompletionHistoryService {

    private final CompletionHistoryRepository completionHistoryRepository;
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
     * 일정 완료 상태 토글
     * @param scheduleId 일정 ID
     * @param date 날짜
     * @param isRecurring 반복 일정 여부
     * @return 업데이트된 완료 이력 DTO
     */
    @Transactional
    public CompletionHistoryDTO toggleCompletion(Long scheduleId, LocalDate date, Boolean isRecurring) {
        Long memberId = getCurrentMemberId();

        // 기존 완료 이력 조회
        Optional<CompletionHistory> existingHistory = completionHistoryRepository.findByScheduleIdAndCompletionDateAndIsRecurring(scheduleId, date, isRecurring);

        if(existingHistory.isPresent()){
            // 이력이 있으면 상태 토글
            CompletionHistory history = existingHistory.get();
            history.toggleCompleted();
            CompletionHistory updated = completionHistoryRepository.save(history);
            return CompletionHistoryDTO.fromEntity(updated);
        } else {
            // 이력이 없으면 새로 생성 (기본적으로 완료 상태로)
            CompletionHistory newHistory = CompletionHistory.builder()
                    .scheduleId(scheduleId)
                    .isRecurring(isRecurring)
                    .completionDate(date)
                    .completed(true) // 첫 토글은 완료상태로
                    .memberId(memberId)
                    .build();

            CompletionHistory saved = completionHistoryRepository.save(newHistory);
            return CompletionHistoryDTO.fromEntity(saved);
        }
    }

    /**
     * 특정 날짜의 모든 완료 이력 조회
     * @param date 날짜
     * @return 완료 이력 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<CompletionHistoryDTO> getCompletionByDate(LocalDate date) {
        Long memberId = getCurrentMemberId();

        List<CompletionHistory> completions =
                completionHistoryRepository.findByCompletionDateAndMemberId(date, memberId);

        return completions.stream()
                .map(CompletionHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 날짜의 완료 상태 맵 조회
     * 키: "[R/S]{scheduleId}", 값: 완료 여부
     * @param date 날짜
     * @return 완료 상태 맵
     */
    @Transactional(readOnly = true)
    public Map<String, Boolean> getCompletionMapByDate(LocalDate date) {
        List<CompletionHistoryDTO> completions = getCompletionByDate(date);
        return completions.stream()
                .collect(Collectors.toMap(
                        c -> (c.getIsRecurring() ? "R" : "S") + c.getScheduleId(),
                        CompletionHistoryDTO::getCompleted
                ));
    }

    /**
     * 특정 일정의 모든 완료 이력 조회
     * @param scheduleId 일정 ID
     * @param isRecurring 반복 일정 여부
     * @return 완료 이력 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<CompletionHistoryDTO> getCompletionsBySchedule(Long scheduleId, Boolean isRecurring) {
        Long memberId = getCurrentMemberId();

        List<CompletionHistory> completions =
                completionHistoryRepository.findByScheduleIdAndIsRecurringAndMemberId(
                        scheduleId, isRecurring, memberId);

        return completions.stream()
                .map(CompletionHistoryDTO::fromEntity)
                .collect(Collectors.toList());
    }
}