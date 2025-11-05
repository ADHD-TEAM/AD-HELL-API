package com.adhd.ad_hell.domain.notification.command.domain.aggregate;

import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "notification_title", nullable = false, length = 150)
    private String notificationTitle;

    @Column(name = "notification_body", nullable = false, columnDefinition = "TEXT")
    private String notificationBody;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_read", nullable = false, length = 1)
    private YnType readYn;

    @Builder
    private Notification(Long userId, String notificationTitle, String notificationBody, YnType readYn) {
        this.userId = userId;
        this.notificationTitle = notificationTitle;
        this.notificationBody = notificationBody;
        this.readYn = readYn == null ? YnType.no() : readYn;
    }

    public void markRead() {
        this.readYn = YnType.yes();
    }
}