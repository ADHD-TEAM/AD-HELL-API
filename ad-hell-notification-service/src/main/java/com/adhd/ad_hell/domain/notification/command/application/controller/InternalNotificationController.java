package com.adhd.ad_hell.domain.notification.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.notification.command.application.dto.request.NotificationSendRequest;
import com.adhd.ad_hell.domain.notification.command.application.dto.response.NotificationDispatchResponse;
import com.adhd.ad_hell.domain.notification.command.application.service.NotificationCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 다른 마이크로서비스(core-service 등)가 호출하는
 * 내부용 알림 발송 전용 컨트롤러.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/notifications")
public class InternalNotificationController {

    private final NotificationCommandService commandService;

    /**
     * 템플릿 기반 알림 발송 (내부 서비스용)
     * - core-service 가 이 엔드포인트를 호출해서 이벤트 알림 등을 발송한다.
     * - 응답은 NotificationDispatchResponse 를 그대로 내려줌.
     */
    @PostMapping("/templates/{templateId}/send")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<NotificationDispatchResponse> sendByTemplate(
            @PathVariable Long templateId,
            @Valid @RequestBody NotificationSendRequest request
    ) {
        NotificationDispatchResponse res = commandService.sendNotification(templateId, request);
        return ApiResponse.success(res);
    }
}
