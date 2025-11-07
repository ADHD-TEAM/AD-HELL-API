package com.adhd.ad_hell.domain.notification.command.application.controller;

import com.adhd.ad_hell.domain.notification.command.application.dto.request.NotificationPushToggleRequest;
import com.adhd.ad_hell.domain.notification.command.application.dto.request.NotificationScheduleRequest;
import com.adhd.ad_hell.domain.notification.command.application.dto.request.NotificationSendRequest;
import com.adhd.ad_hell.domain.notification.command.application.dto.request.NotificationTemplateCreateRequest;
import com.adhd.ad_hell.domain.notification.command.application.dto.request.NotificationTemplateUpdateRequest;
import com.adhd.ad_hell.domain.notification.command.application.dto.response.NotificationDispatchResponse;
import com.adhd.ad_hell.domain.notification.command.application.dto.response.NotificationScheduleResponse;
import com.adhd.ad_hell.domain.notification.command.application.dto.response.NotificationTemplateResponse;
import com.adhd.ad_hell.domain.notification.command.application.service.NotificationCommandService;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationScheduleStatus;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationTemplateKind;
import com.adhd.ad_hell.security.NotificationUserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationCommandController.class)
@AutoConfigureMockMvc(addFilters = false)   // 필터 체인 비활성화 (JWT 필터 등)
class NotificationCommandControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    NotificationCommandService commandService;

    @DisplayName("PATCH /api/notifications/settings/push - 푸시 설정 변경 시 200 OK")
    @Test
    void updatePushSetting() throws Exception {
        // given
        String body = """
                {
                  "memberId": 100,
                  "pushEnabled": true
                }
                """;

        setAuth(userAuth(100L));
        try {
            // when & then
            mockMvc.perform(patch("/api/notifications/settings/push")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk());

            verify(commandService).updatePushSetting(any(NotificationPushToggleRequest.class));
        } finally {
            clearAuth();
        }
    }

    @DisplayName("PATCH /api/users/{userId}/notifications/{notificationId}/read - 본인 요청 시 200 OK")
    @Test
    void markRead() throws Exception {
        Long userId = 1L;
        Long notificationId = 10L;

        setAuth(userAuth(userId));
        try {
            mockMvc.perform(patch("/api/users/{userId}/notifications/{notificationId}/read", userId, notificationId))
                    .andExpect(status().isOk());

            verify(commandService).markRead(eq(userId), eq(notificationId));
        } finally {
            clearAuth();
        }
    }

    @DisplayName("POST /api/admin/notifications/{templateId}/send - ADMIN 권한으로 202 Accepted")
    @Test
    void sendNotification() throws Exception {
        Long templateId = 1L;

        NotificationDispatchResponse serviceRes = NotificationDispatchResponse.builder()
                .notificationId(10L)
                .recipientCount(2)
                .build();

        when(commandService.sendNotification(eq(templateId), any(NotificationSendRequest.class)))
                .thenReturn(serviceRes);

        String body = """
                {
                  "targetType": "PUSH_ENABLED",
                  "variables": {
                    "name": "홍길동"
                  }
                }
                """;

        setAuth(adminAuth());
        try {
            mockMvc.perform(post("/api/admin/notifications/{templateId}/send", templateId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.data.notificationId").value(10L))
                    .andExpect(jsonPath("$.data.recipientCount").value(2));

            verify(commandService).sendNotification(eq(templateId), any(NotificationSendRequest.class));
        } finally {
            clearAuth();
        }
    }

    @DisplayName("POST /api/admin/notifications/{templateId}/reserve - ADMIN 권한으로 201 Created")
    @Test
    void reserveNotification() throws Exception {
        Long templateId = 1L;
        LocalDateTime scheduledAt = LocalDateTime.of(2030, 1, 1, 10, 0, 0);

        NotificationScheduleResponse serviceRes = NotificationScheduleResponse.builder()
                .scheduleId(99L)
                .scheduleStatus(NotificationScheduleStatus.SCHEDULED)
                .scheduledAt(scheduledAt)
                .sentAt(null)
                .build();

        when(commandService.reserveNotification(eq(templateId), any(NotificationScheduleRequest.class)))
                .thenReturn(serviceRes);

        String body = """
                {
                  "scheduledAt": "2030-01-01T10:00:00"
                }
                """;

        setAuth(adminAuth());
        try {
            mockMvc.perform(post("/api/admin/notifications/{templateId}/reserve", templateId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.scheduleId").value(99L))
                    .andExpect(jsonPath("$.data.scheduleStatus").value("SCHEDULED"));

            verify(commandService).reserveNotification(eq(templateId), any(NotificationScheduleRequest.class));
        } finally {
            clearAuth();
        }
    }

    @DisplayName("POST /api/admin/notifications/templates - ADMIN 권한으로 201 Created")
    @Test
    void createTemplate() throws Exception {
        NotificationTemplateResponse serviceRes = NotificationTemplateResponse.builder()
                .templateId(1L)
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("공지 제목")
                .templateBody("공지 내용입니다.")
                .build();

        when(commandService.createTemplate(any(NotificationTemplateCreateRequest.class)))
                .thenReturn(serviceRes);

        setAuth(adminAuth());
        try {
            mockMvc.perform(post("/api/admin/notifications/templates")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "templateKind": "NORMAL",
                                      "templateTitle": "공지 제목",
                                      "templateBody": "공지 내용입니다."
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.templateId").value(1L))
                    .andExpect(jsonPath("$.data.templateKind").value("NORMAL"))
                    .andExpect(jsonPath("$.data.templateTitle").value("공지 제목"))
                    .andExpect(jsonPath("$.data.templateBody").value("공지 내용입니다."));

            verify(commandService).createTemplate(any(NotificationTemplateCreateRequest.class));
        } finally {
            clearAuth();
        }
    }

    @DisplayName("PUT /api/admin/notifications/templates/{templateId} - ADMIN 권한으로 200 OK")
    @Test
    void updateTemplate() throws Exception {
        Long templateId = 5L;

        NotificationTemplateResponse serviceRes = NotificationTemplateResponse.builder()
                .templateId(templateId)
                .templateKind(NotificationTemplateKind.EVENT)
                .templateTitle("수정된 제목")
                .templateBody("수정된 내용입니다.")
                .build();

        when(commandService.updateTemplate(eq(templateId), any(NotificationTemplateUpdateRequest.class)))
                .thenReturn(serviceRes);

        String body = """
                {
                  "templateKind": "EVENT",
                  "templateTitle": "수정된 제목",
                  "templateBody": "수정된 내용입니다."
                }
                """;

        setAuth(adminAuth());
        try {
            mockMvc.perform(put("/api/admin/notifications/templates/{templateId}", templateId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.templateId").value(templateId))
                    .andExpect(jsonPath("$.data.templateKind").value("EVENT"))
                    .andExpect(jsonPath("$.data.templateTitle").value("수정된 제목"))
                    .andExpect(jsonPath("$.data.templateBody").value("수정된 내용입니다."));

            verify(commandService).updateTemplate(eq(templateId), any(NotificationTemplateUpdateRequest.class));
        } finally {
            clearAuth();
        }
    }

    @DisplayName("DELETE /api/admin/notifications/templates/{templateId} - ADMIN 권한으로 200 OK")
    @Test
    void deleteTemplate() throws Exception {
        Long templateId = 7L;

        setAuth(adminAuth());
        try {
            mockMvc.perform(delete("/api/admin/notifications/templates/{templateId}", templateId))
                    .andExpect(status().isOk());

            verify(commandService).deleteTemplate(eq(templateId));
        } finally {
            clearAuth();
        }
    }

    // ====== SecurityContext 헬퍼 ======

    private void setAuth(UsernamePasswordAuthenticationToken auth) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    private void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    private UsernamePasswordAuthenticationToken userAuth(Long userId) {
        NotificationUserPrincipal principal =
                new NotificationUserPrincipal(userId, "user" + userId, "USER");
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }

    private UsernamePasswordAuthenticationToken adminAuth() {
        NotificationUserPrincipal principal =
                new NotificationUserPrincipal(1L, "adminUser", "ADMIN");
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }
}
