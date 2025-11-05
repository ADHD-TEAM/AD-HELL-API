package com.adhd.ad_hell.domain.notification.command.domain.event;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationCreatedEvent {

    private final Long userId;
    private final Long notificationId;
    private final String title;
    private final String body;
    private final LocalDateTime createdAt;
    private final long unreadCount;

    public static NotificationCreatedEvent of(
            Long userId, Long notificationId, String title, String body,
            LocalDateTime createdAt, long unreadCount
    ) {
        return NotificationCreatedEvent.builder()
                .userId(userId)
                .notificationId(notificationId)
                .title(title)
                .body(body)
                .createdAt(createdAt)
                .unreadCount(unreadCount)
                .build();
    }
}
