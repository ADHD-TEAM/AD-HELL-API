package com.adhd.ad_hell.domain.notification.command.infrastructure.sse;

import com.adhd.ad_hell.domain.notification.command.domain.event.NotificationCreatedEvent;
import com.adhd.ad_hell.domain.notification.command.domain.event.NotificationReadEvent;
import com.adhd.ad_hell.domain.notification.command.infrastructure.sse.dto.NotificationEvent;
import com.adhd.ad_hell.domain.notification.command.infrastructure.sse.dto.UnreadCountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationSseListener {

    private final NotificationSseEmitters emitters;

    /** 알림 생성(발송) 완료 후: 새 알림 payload + 미읽음 카운트 동시 푸시 */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotificationCreated(NotificationCreatedEvent e) {
        var payload = NotificationEvent.builder()
                .notificationId(e.getNotificationId())
                .notificationTitle(e.getTitle())
                .notificationBody(e.getBody())
                .read(false)
                .createdAt(e.getCreatedAt())
                .build();

        emitters.sendToUser(e.getUserId(), "NOTIFICATION", payload);
        emitters.sendToUser(e.getUserId(), "UNREAD_COUNT", new UnreadCountEvent(e.getUnreadCount()));
    }

    /** 읽음 처리 완료 후: 갱신된 미읽음 카운트만 푸시 */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotificationRead(NotificationReadEvent e) {
        emitters.sendToUser(e.getUserId(), "UNREAD_COUNT", new UnreadCountEvent(e.getUnreadCount()));
    }
}
