package com.adhd.ad_hell.domain.notification.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationDispatchResponse {
    private final Long notificationId;
    private final int recipientCount;
}