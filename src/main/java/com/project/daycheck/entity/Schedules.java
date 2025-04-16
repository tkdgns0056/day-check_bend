package com.project.daycheck.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static jakarta.persistence.ConstraintMode.*;


/**
 * 일반 일정 엔티티
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

//    @Column
//    private String recurrencePattern;  // DAILY, WEEKLY, WEEKDAY 등

//    @Column
//    private Long parentScheduleId;  // 반복 일정의 부모 ID

    @Column
    private String priority;  // high, medium, low

    @Column(columnDefinition = "TEXT")  // TEXT 타입으로 정의
    private String description;  // 설명 필드

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    /**
     * 일정 완료 상태 토글
     */
    public boolean toggleComplete() {
        this.completed = !this.completed;
        return this.completed;
    }

    /**
     * 일정 내용 수정
     */
    public void updateContent(String content){
        if(content != null && !content.isBlank()){
            this.content = content;
        }
    }

    /**
     * 일정 설명 수정
     */
    public void updateDescription(String description) {
        this.description = description;
    }

    /**
     * 일정 우선순위 수정
     */
    public void updatePriority(String priority){
        if(priority != null && !priority.isBlank()) {
            this.priority = priority;
        }
    }

    /**
     * 일정 시간 수정
     */
    public void updateTimes(LocalDateTime startDate, LocalDateTime endDate) {
        if(startDate != null) {
            this.startDate = startDate;
        }
        if(endDate != null) {
            this.endDate = endDate;
        }
    }
}