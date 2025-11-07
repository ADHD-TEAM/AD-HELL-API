package com.adhd.ad_hell.external.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * notification-service 가 반환하는
 * NotificationDispatchResponse 의 JSON 구조와 맞춘 DTO.
 */
@Getter
@NoArgsConstructor
public class NotificationDispatchResponseDto {

    private Long notificationId;
    private int recipientCount;
}
