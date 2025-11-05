package com.adhd.ad_hell.domain.notification.command.infrastructure.sse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class NotificationSseEmittersTest {

    @Test
    @DisplayName("add(userId) 호출 시 SseEmitter 가 생성되고 이후 sendToUser, broadcast 호출 시 예외 없이 동작한다")
    void addAndSendAndBroadcast() {
        // given
        NotificationSseEmitters sut = new NotificationSseEmitters();
        Long userId1 = 1L;
        Long userId2 = 2L;

        // when: 두 유저에 대해 SSE 연결 등록
        SseEmitter emitter1 = sut.add(userId1);
        SseEmitter emitter2 = sut.add(userId2);

        // then
        assertNotNull(emitter1);
        assertNotNull(emitter2);

        // 예외만 안 나면 OK (INIT 이벤트 전송 등 라인 커버용)
        sut.sendToUser(userId1, "TEST_EVENT", "hello");
        sut.broadcast("BROADCAST_EVENT", "all");
    }

    @Test
    @DisplayName("sendToUser 호출 시 예외가 발생하는 emitter 는 내부 레지스트리에서 제거된다")
    @SuppressWarnings("unchecked")
    void sendToUserRemovesDeadEmitters() throws Exception {
        // given
        NotificationSseEmitters sut = new NotificationSseEmitters();
        Long userId = 100L;

        // 리플렉션으로 private Map<Long, Set<SseEmitter>> emittersByUser 에 접근
        Field field = NotificationSseEmitters.class.getDeclaredField("emittersByUser");
        field.setAccessible(true);
        Map<Long, Set<SseEmitter>> map =
                (Map<Long, Set<SseEmitter>>) field.get(sut);

        // 항상 예외를 던지는 SseEmitter 구현
        SseEmitter errorEmitter = new SseEmitter() {
            @Override
            public void send(SseEventBuilder builder) throws IOException {
                throw new IOException("boom");
            }
        };

        // userId 에 대해 이 에러 emitter 를 등록
        map.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
                .add(errorEmitter);

        // when: 이 유저에게 이벤트를 보내면 send 중 예외가 발생 -> dead 리스트에 담겨 remove 호출
        sut.sendToUser(userId, "EVT", "data");

        // then: dead emitter 가 제거되었는지 확인
        Set<SseEmitter> set = map.get(userId);
        assertTrue(set == null || set.isEmpty(),
                "예외가 발생한 emitter 는 제거되어야 한다.");
    }
}
