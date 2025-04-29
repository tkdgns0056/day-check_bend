package com.project.daycheck.repository;

import com.project.daycheck.entity.RecurringScheduleDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecurringScheduleDayRepository extends JpaRepository<RecurringScheduleDay, Long> {

    // 반복 일정 ID로 요일 정보 조회
    List<RecurringScheduleDay> findByRecurringScheduleId(Long recurringScheduleId);

    // 특정 요일의 모든 반복 일정 ID 조회
    @Query("SELECT rsd.recurringScheduleId FROM RecurringScheduleDay rsd WHERE rsd.dayOfWeek = :dayOfWeek")
    List<Long> findRecurringScheduleIdsByDayOfWeek(@Param("dayOfWeek") DayOfWeek dayOfWeek);

    // 특정 반복 일정의 특정 요일 정보 조회
    Optional<RecurringScheduleDay> findByRecurringScheduleIdAndDayOfWeek(Long recurringScheduleId, DayOfWeek dayOfWeek);

    // 특정 반복 일정의 모든 요일 정보 삭제
    void deleteByRecurringScheduleId(Long recurringScheduleId);
}