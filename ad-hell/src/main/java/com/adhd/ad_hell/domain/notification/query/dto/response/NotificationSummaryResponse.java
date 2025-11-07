package com.adhd.ad_hell.domain.notification.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationSummaryResponse {

    private final Long notificationId;
    private final String notificationTitle;
    private final String notificationBody;
    private final boolean read;
    private final LocalDateTime createdAt;
}