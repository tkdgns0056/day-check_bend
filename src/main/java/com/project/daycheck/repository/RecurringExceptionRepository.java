package com.project.daycheck.repository;

import com.project.daycheck.entity.RecurringException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecurringExceptionRepository extends JpaRepository<RecurringException, Long> {

    // 특정 반복 일정의 모든 예외 조회
    List<RecurringException> findByRecurringScheduleId(Long recurringScheduleId);

    // 특정 날짜의 예외 조회
    Optional<RecurringException> findByRecurringScheduleIdAndExceptionDate(Long recurringScheduleId, LocalDate exceptionDate);

    // 특정 날짜 범위의 예외 조회
    List<RecurringException> findByRecurringScheduleIdAndExceptionDateBetween(Long recurringScheduleId, LocalDate startDate, LocalDate endDate);

    // 특정 예외 유형의 예외 조회
    List<RecurringException> findByRecurringScheduleIdAndExceptionType(Long recurringScheduleId, String exceptionType);

    // 특정 멤버의 특정 날짜 예외 조회
    @Query("SELECT e FROM RecurringException e JOIN e.recurringSchedule rs " +
            "WHERE rs.memberId = :memberId AND e.exceptionDate = :date")
    List<RecurringException> findByMemberIdAndDate(@Param("memberId") Long memberId, @Param("date") LocalDate date);

    // 특정 기간 내 모든 예외 삭제
    void deleteByRecurringScheduleIdAndExceptionDateBetween(Long recurringScheduleId, LocalDate startDate, LocalDate endDate);
}