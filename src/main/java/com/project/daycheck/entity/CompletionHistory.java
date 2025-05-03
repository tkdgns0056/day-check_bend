package com.project.daycheck.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "schedule_completion_history")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompletionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long scheduleId; // 일반 또는 반복 일정 ID

    @Column(nullable = false)
    private Boolean isRecurring; // 반복 일정 여부

    @Column(nullable = false)
    private LocalDate completionDate; // 완료한 날짜

    @Column(nullable = false)
    private Boolean completed; // 완료 상태

    @Column(nullable = false)
    private Long memberId; // 사용자 ID

    @CreationTimestamp
    private LocalDateTime createAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * 완료 상태 토글 메서드
     */
    public boolean toggleCompleted(){
        this.completed = !this.completed;
        return this.completed;
    }
}
