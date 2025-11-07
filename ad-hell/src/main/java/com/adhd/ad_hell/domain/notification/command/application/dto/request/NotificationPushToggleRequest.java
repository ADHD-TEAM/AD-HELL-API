package com.adhd.ad_hell.domain.notification.command.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationPushToggleRequest {

    @NotNull(message = "회원 식별자는 필수 값입니다.")
    private Long memberId;

    @NotNull(message = "푸시 알림 여부는 필수 값입니다.")
    private Boolean pushEnabled;

    @Builder
    private NotificationPushToggleRequest(Long memberId, Boolean pushEnabled) {
        this.memberId = memberId;
        this.pushEnabled = pushEnabled;
    }
}