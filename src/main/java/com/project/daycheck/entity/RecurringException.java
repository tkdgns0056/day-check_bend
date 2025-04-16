package com.project.daycheck.entity;

import jakarta.persistence.*;
import lombok.*;
import org.eclipse.angus.mail.imap.ACL;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 반복 일정 예외 엔티티
 * 특정 날짜에 반복 일정을 건너뛰거나 수정하는 데 사용
 */

@Entity
@Getter
@Builder
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecurringException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurring_schedule_id", nullable = false)
    private RecurringSchedule recurringSchedule;

    @Column(nullable = false)
    private LocalDateTime exceptionDate; // 예외가 적용되는 날짜

    @Column(nullable = false)
    private String exceptionType; // SKIP(건너뛰기), MODIFY(수정)

    // 수정된 내용 (exceptionType이 MODIFY인 경우)
    @Column
    private String modifiedTitle;

    @Column
    private String modifiedStartTime;

    @Column
    private String modifiedEndTime;

    @Column
    private String modifiedPriority;

    @Column(columnDefinition = "TEXT")
    private String modifiedDescription;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * 반복 일정 설정
     * 양방향 관계 설정용
     */
    public void setRecurringSchedule(RecurringSchedule recurringSchedule) {
        this.recurringSchedule = recurringSchedule;
    }

    /**
     * 수정된 제목 업데이트
     */
    public void updateModifiedTitle(String modifiedTitle) {
        this.modifiedTitle = modifiedTitle;
    }

    /**
     * 수정된 시간 업데이트
     */
    public void updateModifiedTimes(String modifiedStartTime, String modifiedEndTime) {
        this.modifiedStartTime = modifiedStartTime;
        this.modifiedEndTime = modifiedEndTime;
    }

    /**
     * 수정된 우선순위 업데이트
     */
    public void updateModifiedPriority(String modifiedPriority) {
        this.modifiedPriority = modifiedPriority;
    }

    /**
     * 수정된 설명 업데이트
     */
    public void updateModifiedDescription(String modifiedDescription) {
        this.modifiedDescription = modifiedDescription;
    }
}
