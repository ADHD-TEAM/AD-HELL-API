// src/test/java/com/adhd/ad_hell/domain/notification/command/application/controller/NotificationCommandControllerTest.java
package com.adhd.ad_hell.domain.notification.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
@AutoConfigureMockMvc(addFilters = false)   // Security 필터 끔
class NotificationCommandControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    NotificationCommandService commandService;

    @DisplayName("PATCH /api/notifications/settings/push - 푸시 설정 변경 요청 시 200 OK 와 함께 서비스가 호출된다")
    @Test
    void updatePushSetting() throws Exception {
        // given
        String body = """
                {
                  "memberId": 100,
                  "pushEnabled": true
                }
                """;

        // when & then
        mockMvc.perform(patch("/api/notifications/settings/push")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        // 서비스 메서드가 DTO를 인자로 한 번 호출되었는지 확인
        verify(commandService).updatePushSetting(any(NotificationPushToggleRequest.class));
    }

    @DisplayName("PATCH /api/users/{userId}/notifications/{notificationId}/read - 읽음 처리 요청 시 200 OK 와 함께 서비스가 호출된다")
    @Test
    void markRead() throws Exception {
        // given
        Long userId = 1L;
        Long notificationId = 10L;

        // when & then
        mockMvc.perform(patch("/api/users/{userId}/notifications/{notificationId}/read", userId, notificationId))
                .andExpect(status().isOk());

        // userId, notificationId 가 그대로 전달되는지 검증
        verify(commandService).markRead(eq(userId), eq(notificationId));
    }

    @DisplayName("POST /api/admin/notifications/{templateId}/send - 즉시 발송 요청 시 202 Accepted 와 NotificationDispatchResponse 가 반환된다")
    @Test
    void sendNotification() throws Exception {
        // given
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

        // when & then
        mockMvc.perform(post("/api/admin/notifications/{templateId}/send", templateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.notificationId").value(10L))
                .andExpect(jsonPath("$.data.recipientCount").value(2));

        verify(commandService).sendNotification(eq(templateId), any(NotificationSendRequest.class));
    }

    @DisplayName("POST /api/admin/notifications/{templateId}/reserve - 예약 발송 요청 시 201 Created 와 NotificationScheduleResponse 가 반환된다")
    @Test
    void reserveNotification() throws Exception {
        // given
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

        // when & then
        mockMvc.perform(post("/api/admin/notifications/{templateId}/reserve", templateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.scheduleId").value(99L))
                .andExpect(jsonPath("$.data.scheduleStatus").value("SCHEDULED"));

        verify(commandService).reserveNotification(eq(templateId), any(NotificationScheduleRequest.class));
    }

    @DisplayName("POST /api/admin/notifications/templates - 템플릿 생성 시 201 Created 와 NotificationTemplateResponse 가 반환된다")
    @Test
    void createTemplate() throws Exception {
        // given: service 가 반환할 mock response 세팅
        NotificationTemplateResponse serviceRes = NotificationTemplateResponse.builder()
                .templateId(1L)
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("공지 제목")
                .templateBody("공지 내용입니다.")
                .build();

        when(commandService.createTemplate(any(NotificationTemplateCreateRequest.class)))
                .thenReturn(serviceRes);

        // when & then: HTTP 요청 보내고 검증
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
    }

    @DisplayName("PUT /api/admin/notifications/templates/{templateId} - 템플릿 수정 시 200 OK 와 수정된 템플릿 정보가 반환된다")
    @Test
    void updateTemplate() throws Exception {
        // given
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

        // when & then
        mockMvc.perform(put("/api/admin/notifications/templates/{templateId}", templateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.templateId").value(templateId))
                .andExpect(jsonPath("$.data.templateKind").value("EVENT"))
                .andExpect(jsonPath("$.data.templateTitle").value("수정된 제목"))
                .andExpect(jsonPath("$.data.templateBody").value("수정된 내용입니다."));

        verify(commandService).updateTemplate(eq(templateId), any(NotificationTemplateUpdateRequest.class));
    }

    @DisplayName("DELETE /api/admin/notifications/templates/{templateId} - 템플릿 삭제 시 200 OK 가 반환되고 서비스가 호출된다")
    @Test
    void deleteTemplate() throws Exception {
        // given
        Long templateId = 7L;

        // when & then
        mockMvc.perform(delete("/api/admin/notifications/templates/{templateId}", templateId))
                .andExpect(status().isOk());

        verify(commandService).deleteTemplate(eq(templateId));
    }
}
