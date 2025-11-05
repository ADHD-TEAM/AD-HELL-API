package com.adhd.ad_hell.domain.notification.command.domain.aggregate;

import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationScheduleStatus;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "notification_schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private NotificationTemplate template;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_status", nullable = false, length = 20)
    private NotificationScheduleStatus scheduleStatus;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_deleted", nullable = false, length = 1)
    private YnType deletedYn;

    @Builder
    private NotificationSchedule(
            NotificationTemplate template,
            NotificationScheduleStatus scheduleStatus,
            LocalDateTime scheduledAt,
            LocalDateTime sentAt,
            YnType deletedYn
    ) {
        this.template = template;
        this.scheduleStatus = scheduleStatus == null ? NotificationScheduleStatus.SCHEDULED : scheduleStatus;
        this.scheduledAt = scheduledAt;
        this.sentAt = sentAt;
        this.deletedYn = deletedYn == null ? YnType.no() : deletedYn;
    }

    public void markCompleted(LocalDateTime sentAt) {
        this.scheduleStatus = NotificationScheduleStatus.COMPLETED;
        this.sentAt = sentAt;
    }

    public void markFailed() {
        this.scheduleStatus = NotificationScheduleStatus.FAILED;
    }

    public void cancel() {
        this.scheduleStatus = NotificationScheduleStatus.CANCELLED;
    }

    public void delete() {
        this.deletedYn = YnType.yes();
    }
}