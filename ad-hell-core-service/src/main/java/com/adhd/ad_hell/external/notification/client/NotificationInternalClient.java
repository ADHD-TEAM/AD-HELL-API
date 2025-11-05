package com.adhd.ad_hell.external.notification.client;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.external.notification.dto.NotificationDispatchResponseDto;
import com.adhd.ad_hell.external.notification.dto.NotificationSendRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

/**
 * AD-HELL-NOTIFICATION-SERVICE 의 내부용 알림 발송 API 호출용 Feign Client.
 *
 * name = 유레카에 등록된 서비스 이름
 * path = InternalNotificationController 의 @RequestMapping("/internal/notifications")
 */
@FeignClient(
        name = "AD-HELL-NOTIFICATION-SERVICE",
        path = "/internal/notifications"
)
@Repository
public interface NotificationInternalClient {

    /**
     * 템플릿 기반 알림 발송 (내부용)
     *
     * POST /internal/notifications/templates/{templateId}/send
     */
    @PostMapping("/templates/{templateId}/send")
    ApiResponse<NotificationDispatchResponseDto> sendByTemplate(
            @PathVariable("templateId") Long templateId,
            @RequestBody NotificationSendRequestDto request
    );
}
