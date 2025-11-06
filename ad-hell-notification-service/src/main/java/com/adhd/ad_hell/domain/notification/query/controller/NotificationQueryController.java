package com.adhd.ad_hell.domain.notification.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationPageResponse;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationTemplatePageResponse;
import com.adhd.ad_hell.domain.notification.query.service.NotificationQueryService;
import com.adhd.ad_hell.domain.notification.command.infrastructure.sse.NotificationSseEmitters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;
import com.adhd.ad_hell.security.NotificationUserPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "Notification Query", description = "알림 조회 및 SSE 구독 API")
public class NotificationQueryController {

    private final NotificationQueryService queryService;
    private final NotificationSseEmitters emitters;

    // 유저 알림 목록(페이지)
    @Operation(
            summary = "유저 알림 목록 조회",
            description = "특정 유저의 알림 목록을 페이지 단위로 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    @GetMapping("/api/users/{userId}/notifications")
    public ResponseEntity<ApiResponse<NotificationPageResponse>> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size,
            @AuthenticationPrincipal NotificationUserPrincipal principal
    ) {
        if (principal == null) {
            throw new AccessDeniedException("인증 정보가 없습니다.");
        }

        Long authUserId = principal.getUserId();
        if (!userId.equals(authUserId)) {
            throw new AccessDeniedException("다른 사용자의 알림은 조회할 수 없습니다.");
        }

        var res = queryService.getUserNotifications(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    // 미읽음 카운트
    @Operation(
            summary = "미읽음 알림 개수 조회",
            description = "특정 유저의 미읽음 알림 개수를 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    @GetMapping("/api/users/{userId}/notifications/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @PathVariable Long userId,
            @AuthenticationPrincipal NotificationUserPrincipal principal
    ) {
        if (principal == null) {
            throw new AccessDeniedException("인증 정보가 없습니다.");
        }

        Long authUserId = principal.getUserId();
        if (!userId.equals(authUserId)) {
            throw new AccessDeniedException("다른 사용자의 알림은 조회할 수 없습니다.");
        }

        long cnt = queryService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(cnt));
    }

    // SSE 구독 (L7 idle 방지 heartbeat는 sse 패키지에서 주기 브로드캐스트)
    @Operation(
            summary = "알림 SSE 스트림 구독",
            description = """
                    해당 유저의 알림 이벤트를 SSE(Server-Sent Events)로 실시간 구독한다.
                    이벤트 타입: INIT, NOTIFICATION, UNREAD_COUNT, PING
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "SSE 스트림 연결 성공",
                    content = @Content(mediaType = "text/event-stream")
            )
    })
    @GetMapping(
            value = "/api/users/{userId}/notifications/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter stream(@PathVariable Long userId /* TODO: @AuthenticationPrincipal 검증 */) {
        return emitters.add(userId);
    }

    // 관리자 템플릿 목록/검색
    @Operation(
            summary = "알림 템플릿 목록/검색",
            description = "삭제되지 않은 알림 템플릿을 페이지 단위로 조회하거나 제목 키워드로 검색한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/notifications/templates")
    public ResponseEntity<ApiResponse<NotificationTemplatePageResponse>> getTemplates(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size
    ) {
        var res = queryService.getTemplates(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(res));
    }
}
