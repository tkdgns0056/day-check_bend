package com.project.daycheck.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static jakarta.persistence.ConstraintMode.*;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "member_id",  insertable = false, updatable = false)
    private Member member;

    @Column(nullable = false)
    private String content;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    @Column
    private Boolean completed;

    @Column
    private String recurrencePattern;  // DAILY, WEEKLY, WEEKDAY 등

    @Column
    private Long parentScheduleId;  // 반복 일정의 부모 ID

    @Column
    private String priority;  // high, medium, low

    @Column(columnDefinition = "TEXT")  // TEXT 타입으로 정의
    private String description;  // 설명 필드

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private Integer  notificationBefore;  // 시작 전 몇 분 전에 알림을 보낼지 설정

}