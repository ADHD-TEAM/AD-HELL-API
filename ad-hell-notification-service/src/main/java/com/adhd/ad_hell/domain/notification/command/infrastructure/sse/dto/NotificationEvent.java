package com.adhd.ad_hell.domain.notification.command.infrastructure.sse.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// SSE 이벤트 DTO (공유 페이로드)
@Getter
@Builder
public class NotificationEvent {
    private final Long notificationId;
    private final String notificationTitle;
    private final String notificationBody;
    private final boolean read;
    private final LocalDateTime createdAt;
}