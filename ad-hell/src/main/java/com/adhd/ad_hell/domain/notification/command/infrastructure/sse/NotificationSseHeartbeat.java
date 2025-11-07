package com.adhd.ad_hell.domain.notification.command.infrastructure.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationSseHeartbeat {
    private final NotificationSseEmitters emitters;


    // 25초마다 핑: L4/L7 idle-timeout 회피용
    @Scheduled(fixedDelay = 25000)
    public void ping() { emitters.broadcast("PING", "keep-alive"); }
}