package com.adhd.ad_hell.domain.notification.command.application.dto.response;


import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationScheduleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationScheduleResponse {
    private final Long scheduleId;
    private final NotificationScheduleStatus scheduleStatus;
    private final LocalDateTime scheduledAt;
    private final LocalDateTime sentAt;
}