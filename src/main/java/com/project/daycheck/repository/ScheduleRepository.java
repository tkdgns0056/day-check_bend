package com.project.daycheck.repository;

import com.project.daycheck.entity.Schedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedules, Long> {

    /**
     * 특정 날짜 범위에 해당하는 일정을 조회합니다.
     * 시작일이 범위 내에 있거나 종료일이 범위 내에 있는 일정을 모두 포함합니다.
     */
    List<Schedules> findByStartDateBetweenOrEndDateBetween(
            LocalDateTime startDateFrom,
            LocalDateTime startDateTo,
            LocalDateTime endDateFrom,
            LocalDateTime endDateTo
    );

    /**
     * 부모 일정 ID로 하위 일정들을 조회합니다.
     */
    List<Schedules> findByParentScheduleId(Long parentScheduleId);

    /**
     * 부모 일정이 아닌 일정들을 조회합니다(parentScheduleId가 null인 것들).
     */
    List<Schedules> findByParentScheduleIdIsNull();

    /**
     * 특정 날짜의 일정을 조회합니다.
     */
    List<Schedules> findByStartDateBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 완료된 일정을 조회합니다.
     */
    List<Schedules> findByCompleted(Boolean completed);

    /**
     * 특정 우선순위의 일정을 조회합니다.
     */
    List<Schedules> findByPriority(String priority);

    /**
     * 특정 반복 패턴의 일정을 조회합니다.
     */
    List<Schedules> findByRecurrencePattern(String recurrencePattern);
}