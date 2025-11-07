package com.adhd.ad_hell.external.notification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * core-service -> notification-service 로 보낼
 * 알림 발송 요청 DTO.
 *
 * notification-service 의 NotificationSendRequest 와
 * JSON 필드 구조/이름이 동일해야 함.
 */
@Getter
@NoArgsConstructor
public class NotificationSendRequestDto {

    private TargetType targetType;        // ALL, PUSH_ENABLED, CUSTOM
    private List<Long> targetMemberIds;   // CUSTOM 일 때 대상 회원 ID 목록
    private Map<String, String> variables; // 템플릿 변수 {{key}} 치환용

    @Builder
    public NotificationSendRequestDto(
            TargetType targetType,
            List<Long> targetMemberIds,
            Map<String, String> variables
    ) {
        this.targetType = targetType;
        this.targetMemberIds = targetMemberIds;
        this.variables = variables;
    }

    public enum TargetType {
        ALL,
        PUSH_ENABLED,
        CUSTOM
    }
}
