package com.project.daycheck.repository;

import com.project.daycheck.entity.CompletionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompletionHistoryRepository  extends JpaRepository<CompletionHistory, Long> {

    // 특정 날자, 특정 일정(일반/반복)의 완료 이력 조회
    Optional<CompletionHistory> findByScheduleIdAndCompletionDateAndIsRecurring(
            Long scheduleId, LocalDate completionDate, Boolean isRecurring);

    // 특정 날짜의 모든 완료 이력 조회
    List<CompletionHistory> findByCompletionDateAndMemberId(LocalDate completionDate, Long memberId);

    // 특정 일정의 모든 완료 이력 조회
    List<CompletionHistory> findByScheduleIdAndIsRecurringAndMemberId(Long scheduleId, Boolean isRecurring, Long memberId);
}
