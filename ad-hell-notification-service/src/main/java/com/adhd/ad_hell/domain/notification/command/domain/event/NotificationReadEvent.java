// ad-hell/src/main/java/com/adhd/ad_hell/domain/notification/event/NotificationReadEvent.java
package com.adhd.ad_hell.domain.notification.command.domain.event;

import lombok.*;
import lombok.Getter;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationReadEvent {

    private final Long userId;
    private final long unreadCount;

    public static NotificationReadEvent of(Long userId, long unreadCount) {
        return NotificationReadEvent.builder()
                .userId(userId)
                .unreadCount(unreadCount)
                .build();
    }
}
