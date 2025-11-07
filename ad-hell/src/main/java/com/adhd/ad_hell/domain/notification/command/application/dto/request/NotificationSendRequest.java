package com.adhd.ad_hell.domain.notification.command.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class NotificationSendRequest {

    @NotNull(message = "발송 대상은 필수 값입니다.")
    private TargetType targetType;

    private List<Long> targetMemberIds;

    private Map<String, String> variables;

    @Builder
    private NotificationSendRequest(TargetType targetType, List<Long> targetMemberIds, Map<String, String> variables) {
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