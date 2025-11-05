package com.adhd.ad_hell.domain.notification.command.infrastructure.sse;

import com.adhd.ad_hell.domain.notification.command.domain.event.NotificationCreatedEvent;
import com.adhd.ad_hell.domain.notification.command.domain.event.NotificationReadEvent;
import com.adhd.ad_hell.domain.notification.command.infrastructure.sse.dto.NotificationEvent;
import com.adhd.ad_hell.domain.notification.command.infrastructure.sse.dto.UnreadCountEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationSseListenerTest {

    @Mock
    NotificationSseEmitters emitters;

    @InjectMocks
    NotificationSseListener sut;

    @Test
    @DisplayName("NotificationCreatedEvent 수신 시 NOTIFICATION + UNREAD_COUNT SSE 이벤트를 전송한다")
    void onNotificationCreated_sendsNotificationAndUnreadCount() {
        // given
        Long userId = 1L;
        Long notificationId = 10L;
        LocalDateTime createdAt = LocalDateTime.now();

        NotificationCreatedEvent event = NotificationCreatedEvent.of(
                userId,
                notificationId,
                "제목",
                "내용",
                createdAt,
                5L
        );

        // when
        sut.onNotificationCreated(event);

        // then
        // 모든 인자를 matcher로 통일 (eq / any)
        verify(emitters).sendToUser(
                eq(userId),
                eq("NOTIFICATION"),
                any(NotificationEvent.class)
        );
        verify(emitters).sendToUser(
                eq(userId),
                eq("UNREAD_COUNT"),
                any(UnreadCountEvent.class)
        );
    }

    @Test
    @DisplayName("NotificationReadEvent 수신 시 UNREAD_COUNT SSE 이벤트만 전송한다")
    void onNotificationRead_sendsUnreadCountOnly() {
        // given
        Long userId = 2L;
        NotificationReadEvent event = NotificationReadEvent.of(userId, 3L);

        // when
        sut.onNotificationRead(event);

        // then
        // 여기서도 마찬가지로 모든 인자를 matcher로 통일
        verify(emitters).sendToUser(
                eq(userId),
                eq("UNREAD_COUNT"),
                any(UnreadCountEvent.class)
        );
    }
}
