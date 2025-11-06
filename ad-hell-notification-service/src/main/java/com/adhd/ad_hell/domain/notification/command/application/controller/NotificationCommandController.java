package com.adhd.ad_hell.domain.notification.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.notification.command.application.dto.request.*;
import com.adhd.ad_hell.domain.notification.command.application.dto.response.NotificationDispatchResponse;
import com.adhd.ad_hell.domain.notification.command.application.dto.response.NotificationScheduleResponse;
import com.adhd.ad_hell.domain.notification.command.application.dto.response.NotificationTemplateResponse;
import com.adhd.ad_hell.domain.notification.command.application.service.NotificationCommandService;
import com.adhd.ad_hell.security.NotificationUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "Notification Command", description = "알림 설정 변경 및 발송/템플릿 관리 API")
public class NotificationCommandController {

    private final NotificationCommandService commandService;

    // --- 사용자 설정/읽음 ---

    @Operation(
            summary = "푸시 알림 on/off 설정",
            description = "회원의 푸시 알림 사용 여부를 on/off로 변경한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "설정 변경 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값 검증 실패"
            )
    })
    @PatchMapping("/api/notifications/settings/push")
    public ResponseEntity<ApiResponse<Void>> updatePushSetting(
            @Valid @RequestBody NotificationPushToggleRequest request,
            @AuthenticationPrincipal NotificationUserPrincipal principal
    ) {
        if (principal == null) {
            throw new AccessDeniedException("인증 정보가 없습니다.");
        }

        Long authUserId = principal.getUserId();
        Long targetUserId = request.getMemberId(); // 필드명이 다르면 여기를 맞춰주면 됨

        if (!authUserId.equals(targetUserId)) {
            throw new AccessDeniedException("다른 사용자의 푸시 설정은 변경할 수 없습니다.");
        }

        commandService.updatePushSetting(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(
            summary = "알림 읽음 처리",
            description = "해당 유저의 특정 알림을 읽음 상태로 변경한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "읽음 처리 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "본인 알림이 아니거나 잘못된 요청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "알림이 존재하지 않음"
            )
    })
    @PatchMapping("/api/users/{userId}/notifications/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(
            @PathVariable Long userId,
            @PathVariable Long notificationId,
            @AuthenticationPrincipal NotificationUserPrincipal principal
    ) {
        if (principal == null) {
            throw new AccessDeniedException("인증 정보가 없습니다.");
        }

        Long authUserId = principal.getUserId();
        if (!authUserId.equals(userId)) {
            throw new AccessDeniedException("다른 사용자의 알림은 읽음 처리할 수 없습니다.");
        }

        commandService.markRead(userId, notificationId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // --- 관리자: 즉시/예약 발송 ---

    @Operation(
            summary = "알림 즉시 발송",
            description = """
                    지정된 템플릿을 사용해 즉시 알림을 발송한다.
                    대상은 ALL / PUSH_ENABLED / CUSTOM 중 하나를 선택한다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "202",
                    description = "발송 요청 수락 (비동기 처리)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값 검증 실패 또는 대상자 계산 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "템플릿이 존재하지 않음"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/notifications/{templateId}/send")
    public ResponseEntity<ApiResponse<NotificationDispatchResponse>> send(
            @PathVariable Long templateId,
            @Valid @RequestBody NotificationSendRequest request
    ) {
        var res = commandService.sendNotification(templateId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(res));
    }

    @Operation(
            summary = "알림 예약 발송 등록",
            description = "특정 시각에 발송될 알림 예약을 등록한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "예약 생성 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "예약 발송 시각이 현재 시각 이전이거나 잘못된 요청"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "템플릿이 존재하지 않음"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/notifications/{templateId}/reserve")
    public ResponseEntity<ApiResponse<NotificationScheduleResponse>> reserve(
            @PathVariable Long templateId,
            @Valid @RequestBody NotificationScheduleRequest request
    ) {
        var res = commandService.reserveNotification(templateId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(res));
    }

    // --- 관리자: 템플릿 CUD ---

    @Operation(
            summary = "알림 템플릿 생성",
            description = "관리자가 신규 알림 템플릿을 등록한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "템플릿 생성 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값 검증 실패"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/notifications/templates")
    public ResponseEntity<ApiResponse<NotificationTemplateResponse>> createTemplate(
            @Valid @RequestBody NotificationTemplateCreateRequest request
    ) {
        var res = commandService.createTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(res));
    }

    @Operation(
            summary = "알림 템플릿 수정",
            description = "관리자가 기존 알림 템플릿 정보를 수정한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "템플릿 수정 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값 검증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "템플릿이 존재하지 않음"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/api/admin/notifications/templates/{templateId}")
    public ResponseEntity<ApiResponse<NotificationTemplateResponse>> updateTemplate(
            @PathVariable Long templateId,
            @Valid @RequestBody NotificationTemplateUpdateRequest request
    ) {
        var res = commandService.updateTemplate(templateId, request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @Operation(
            summary = "알림 템플릿 삭제",
            description = "관리자가 알림 템플릿을 삭제한다. (soft delete)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "템플릿 삭제(비활성화) 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "템플릿이 존재하지 않음"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/admin/notifications/templates/{templateId}")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(
            @PathVariable Long templateId
    ) {
        commandService.deleteTemplate(templateId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
