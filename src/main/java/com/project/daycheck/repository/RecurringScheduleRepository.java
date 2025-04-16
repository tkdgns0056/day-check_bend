package com.project.daycheck.repository;

import com.project.daycheck.entity.RecurringSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecurringScheduleRepository extends JpaRepository<RecurringSchedule, Long> {

    // 멤버ID로 반복 일정 조회
    List<RecurringSchedule> findByMemberId(Long memberId);

    // ID와 멤버 ID로 특정 반복 일정 조회
    Optional<RecurringSchedule> findByIdAndMemberId(Long id, Long memberId);

    // 특정 날짜에 적용되는 반복 일정 조회
    @Query("SELECT rs FROM RecurringSchedule rs WHERE" +
            "rs.memberId = :memberId AND " +
            "rs.startDate <= :date AND " +
            "(rs.endDate IS NULL OR rs.endDate >= :date)")
    List<RecurringSchedule> findActiveOnDate(@Param("memberId") Long memberId, @Param("date") LocalDateTime date);

    // 특정 날짜 범위에 적용되는 반복 일정 조회
    @Query("SELECT rs FROM RecurringSchedule rs WHERE " +
            "rs.memberId = :memberId AND " +
            "rs.startDate <= :endDate AND " +
            "(rs.endDate IS NULL OR rs.endDate >= :startDate)")
    List<RecurringSchedule> findActiveInDateRange(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 특정 패턴 유형의 반복 일정 조회
    List<RecurringSchedule> findByMemberIdAndPatternType(Long memberId, String patternType);

    // 제목으로 반복 일정 검색
    List<RecurringSchedule> findByMemberIdAndTitleContaining(Long memberId, String keyword);

    // 우선순위별 반복 일정 조회
    List<RecurringSchedule> findByMemberIdAndPriority(Long memberId, String priority);
}
