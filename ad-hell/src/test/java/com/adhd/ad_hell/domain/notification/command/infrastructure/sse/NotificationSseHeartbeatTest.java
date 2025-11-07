package com.adhd.ad_hell.domain.notification.command.infrastructure.sse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationSseHeartbeatTest {

    @Mock
    NotificationSseEmitters emitters;

    @InjectMocks
    NotificationSseHeartbeat sut;

    @Test
    @DisplayName("ping() 호출 시 emitters.broadcast(\"PING\", \"keep-alive\") 를 호출한다")
    void pingBroadcastsKeepAlive() {
        // when
        sut.ping();

        // then
        verify(emitters).broadcast("PING", "keep-alive");
    }
}
