package com.project.daycheck.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Array;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 반복 일정 엔티티
 * 특정 패턴으로 반복되는 일정을 나타냄
 */
@Entity
@Table(name = "recurring_schedule")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecurringSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    @Column(nullable = false)
    private String content; // 반복 일정 내용

    @Column(nullable = false)
    private String patternType; // DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM 등등

    @Column(name = "`interval`", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer interval; // 반복 간격(ex. 2주마다, 3개월마다)

    @Column
    private Integer dayOfMonth; // 매월 n일

    @Column
    private Integer weekOfMonth; // 매월 n번쨰 주

    @Column
    private LocalDateTime startDate; // 전체 반복 시작

    @Column
    private LocalDateTime endDate; // 전체 반복 종료

    @Column
    private String startTime; // 각 일정의 시작 시간(HH:mm 형식)

    @Column
    private String endTime; // 각 일정의 종료 시간(HH:mm 형식)

    @Column
    private String priority; // high, medium, low

    @Column(columnDefinition = "TEXT")
    private String description; // 일정 설명

    @OneToMany(mappedBy = "recurringSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecurringScheduleDay> scheduleDays = new ArrayList<>();

    @OneToMany(mappedBy = "recurringSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecurringException> exceptions = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createAt;

    @UpdateTimestamp
    private LocalDateTime updateAt;

    /**
     * 요일 추가 메소드
     */
    public void addDay(DayOfWeek dayOfWeek) {
        RecurringScheduleDay scheduleDay = RecurringScheduleDay.builder()
                .recurringScheduleId(this.id)
                .dayOfWeek(dayOfWeek)
                .build();

        scheduleDays.add(scheduleDay);
        scheduleDay.setRecurringSchedule(this);
    }

    /**
     * 요일 제거 메소드
     */
    public void removeDay(DayOfWeek dayOfWeek) {
        scheduleDays.removeIf(day -> day.getDayOfWeek() == dayOfWeek);
    }

    /**
     * 특정 요일이 포함되어 있는지 확인
     */
    public boolean containsDay(DayOfWeek dayOfWeek) {
        return scheduleDays.stream()
                .anyMatch(day -> day.getDayOfWeek() == dayOfWeek);
    }

    /**
     * 반복 일정 제목 수정
     */
    public void updateContent(String content){
        if(content != null && !content.isBlank()){
            this.content = content;
        }
    }

    /**
     * 반복 일정 설명 수정
     */
    public void updateDescription(String description){
        this.description = description;
    }

    /**
     *  반복 일정 우선순위 수정
     */
    public void updatePriority(String priority) {
        if(priority != null && !priority.isBlank()){
            this.priority = priority;
        }
    }

    /**
     *  반복 일정 기간 수정
     */
    public void updateDateRange(LocalDateTime startDate, LocalDateTime endDate){
        if(startDate != null) {
            this.startDate = startDate;
        }
        if(endDate != null) {
            this.endDate = endDate;
        }
    }

    /**
     * 반복 일정 시간 수정
     */
    public void updateTimes(String startTime, String endTime) {
        if (startTime != null && !startTime.isBlank()) {
            this.startTime = startTime;
        }
        if (endTime != null && !endTime.isBlank()) {
            this.endTime = endTime;
        }
    }

    /**
     * 반복 간격 수정
     */
    public void updateInterval(Integer interval) {
        if (interval != null && interval > 0) {
            this.interval = interval;
        }
    }

    /**
     * 월 기준 날짜 수정 (MONTHLY 패턴용)
     */
    public void updateDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    /**
     * 월 기준 주 수정 (MONTHLY 패턴용)
     */
    public void updateWeekOfMonth(Integer weekOfMonth) {
        this.weekOfMonth = weekOfMonth;
    }

    /**
     * 예외 추가
     */
    public void addException(RecurringException exception) {
        exceptions.add(exception);
        exception.setRecurringSchedule(this);
    }

}
