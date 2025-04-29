package com.project.daycheck.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

/**
 * 반복 일정 요일 엔티티
 * 반복 일정의 요일 정보를 저장
 */
@Entity
@Table(name = "recurring_schedule_day")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecurringScheduleDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recurring_schedule_id", nullable = false)
    private Long recurringScheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurring_schedule_id", insertable = false, updatable = false)
    private RecurringSchedule recurringSchedule;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * 반복 일정 설정 메소드
     * 양방향 관계 설정용
     */
    public void setRecurringSchedule(RecurringSchedule recurringSchedule) {
        this.recurringSchedule = recurringSchedule;
    }
}