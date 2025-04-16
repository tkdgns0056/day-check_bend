package com.project.daycheck.repository;

import com.project.daycheck.entity.Schedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedules, Long> {

    // memberId로 일정 찾기
    List<Schedules> findByMemberId(Long memberId);

    // ID와 memberId로 특정 일정 찾기
    Optional<Schedules> findByIdAndMemberId(Long id, Long memberId);

    // 특정 멤버의 특정 날짜 범위에 해당하는 일정을 조회합니다.
    @Query("SELECT s FROM Schedules s WHERE " +
            "(" +
            "   (s.startDate BETWEEN :startDate AND :endDate) OR " +
            "   (s.endDate BETWEEN :startDate AND :endDate) OR " +
            "   (s.startDate <= :startDate AND s.endDate >= :endDate)" +
            ") AND s.memberId = :memberId")
    List<Schedules> findSchedulesForDateRangeAndMember(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("memberId") Long memberId
    );

    // 특정 멤버의 부모 일정 ID로 하위 일정들을 조회 - version1
//    List<Schedules> findByParentScheduleIdAndMemberId(Long parentScheduleId, Long memberId);

    // 특정 멤버의 부모가 아닌 일정들을 조회합니다(parentScheduleId가 null인 것들) - version1
//    List<Schedules> findByMemberIdAndParentScheduleIdIsNull(Long memberId);



//     // 특정 날짜 범위에 해당하는 일정을 조회합니다.
//     // 시작일이 범위 내에 있거나 종료일이 범위 내에 있는 일정을 모두 포함
//    List<Schedules> findByStartDateBetweenOrEndDateBetween(
//            LocalDateTime startDateFrom,
//            LocalDateTime startDateTo,
//            LocalDateTime endDateFrom,
//            LocalDateTime endDateTo
//    );
//
//
//     //부모 일정 ID로 하위 일정들을 조회
//    List<Schedules> findByParentScheduleId(Long parentScheduleId);
//
//
//     // 부모 일정이 아닌 일정들을 조회합니다(parentScheduleId가 null인 것들)
//    List<Schedules> findByParentScheduleIdIsNull();
//
//     // 완료된 일정을 조회
//    List<Schedules> findByCompleted(Boolean completed);
//
//    // 2025.03.21 psh - 시작 시간 기준으로 일정 조회
//    List<Schedules> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
//
//    // 2025.03.21 psh - 특정 시간 이후의 모든 일정 조회
//    List<Schedules> findByStartDateAfter(LocalDateTime dateTime);
//
//    // 2025.03.21 psh - 진행 중인 일정 조회
//    @Query("SELECT s FROM Schedules s WHERE s.startDate <= ?1 AND s.endDate >= ?1 AND s.completed = false")
//    List<Schedules> findActiveSchedules(LocalDateTime now);
}