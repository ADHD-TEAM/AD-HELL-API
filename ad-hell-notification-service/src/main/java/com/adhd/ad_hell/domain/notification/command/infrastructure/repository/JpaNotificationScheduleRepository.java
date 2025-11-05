package com.adhd.ad_hell.domain.notification.command.infrastructure.repository;

import com.adhd.ad_hell.domain.notification.command.domain.aggregate.NotificationSchedule;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaNotificationScheduleRepository extends JpaRepository<NotificationSchedule, Long> {

    List<NotificationSchedule> findByScheduleStatusAndScheduledAtLessThanEqual(
            NotificationScheduleStatus scheduleStatus,
            LocalDateTime scheduledAt
    );
}
