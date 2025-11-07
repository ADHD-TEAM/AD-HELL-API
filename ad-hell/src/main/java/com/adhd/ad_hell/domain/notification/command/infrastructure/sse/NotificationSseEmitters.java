package com.adhd.ad_hell.domain.notification.command.infrastructure.sse;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// SSE Emitter 레지스트리
@Component
public class NotificationSseEmitters {


    private static final long TIMEOUT_MS = 60L * 60 * 1000; // 1시간
    private final Map<Long, Set<SseEmitter>> emittersByUser = new ConcurrentHashMap<>();


    public SseEmitter add(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        emittersByUser.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(emitter);


        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(e -> remove(userId, emitter));


        try {
            emitter.send(SseEmitter.event().name("INIT").data("ok", MediaType.TEXT_PLAIN));
        } catch (IOException ignored) {}
        return emitter;
    }


    public void sendToUser(Long userId, String eventName, Object data) {
        Set<SseEmitter> set = emittersByUser.getOrDefault(userId, Set.of());
        List<SseEmitter> dead = new ArrayList<>();
        for (SseEmitter em : set) {
            try { em.send(SseEmitter.event().name(eventName).data(data)); }
            catch (Exception e) { dead.add(em); }
        }
        dead.forEach(em -> remove(userId, em));
    }


    public void broadcast(String eventName, Object data) {
        for (Long userId : emittersByUser.keySet()) {
            sendToUser(userId, eventName, data);
        }
    }


    private void remove(Long userId, SseEmitter emitter) {
        Set<SseEmitter> set = emittersByUser.get(userId);
        if (set != null) {
            set.remove(emitter);
            if (set.isEmpty()) emittersByUser.remove(userId);
        }
    }
}