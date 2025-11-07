package com.adhd.ad_hell.domain.notification.command.application.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NotificationScheduleRequest {

    @NotNull(message = "예약 발송 시각은 필수 값입니다.")
    @Future(message = "예약 발송 시각은 현재 이후여야 합니다.")
    private LocalDateTime scheduledAt;

    @Builder
    private NotificationScheduleRequest(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}