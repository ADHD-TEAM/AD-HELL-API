package com.adhd.ad_hell.domain.notification.command.infrastructure.sse.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

// SSE 이벤트 DTO (뱃지 카운트)
@Getter
@AllArgsConstructor
public class UnreadCountEvent {
    private final long count;
}