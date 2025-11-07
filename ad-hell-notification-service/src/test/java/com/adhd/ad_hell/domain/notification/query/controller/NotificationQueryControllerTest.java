package com.adhd.ad_hell.domain.notification.query.controller;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationTemplateKind;
import com.adhd.ad_hell.domain.notification.command.infrastructure.sse.NotificationSseEmitters;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationPageResponse;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationSummaryResponse;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationTemplatePageResponse;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationTemplateSummaryResponse;
import com.adhd.ad_hell.domain.notification.query.service.NotificationQueryService;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationQueryController.class)
@AutoConfigureMockMvc(addFilters = false) // JWT 필터 등은 끄고, SecurityContext만 수동으로 세팅
class NotificationQueryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    NotificationQueryService queryService;

    // SSE 의존성도 MockBean 필요
    @MockBean
    NotificationSseEmitters emitters;

    @Test
    @DisplayName("GET /api/users/{userId}/notifications - 본인 요청 시 알림 목록과 페이징 정보가 ApiResponse 로 반환된다")
    void getUserNotificationsSuccess() throws Exception {
        // given
        Long userId = 100L;
        int page = 0;
        int size = 10;

        NotificationSummaryResponse n1 = NotificationSummaryResponse.builder()
                .notificationId(1L)
                .notificationTitle("알림1")
                .notificationBody("내용1")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        NotificationSummaryResponse n2 = NotificationSummaryResponse.builder()
                .notificationId(2L)
                .notificationTitle("알림2")
                .notificationBody("내용2")
                .read(true)
                .createdAt(LocalDateTime.now())
                .build();

        Pagination pagination = Pagination.builder()
                .currentPage(page)
                .totalPages(1)
                .totalItems(2L)
                .build();

        NotificationPageResponse pageResponse = NotificationPageResponse.builder()
                .notifications(List.of(n1, n2))
                .pagination(pagination)
                .build();

        given(queryService.getUserNotifications(eq(userId), eq(page), eq(size)))
                .willReturn(pageResponse);

        // 본인 인증 세팅
        setAuth(userAuth(userId));

        try {
            // when & then
            mockMvc.perform(get("/api/users/{userId}/notifications", userId)
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    // ApiResponse<T> 의 data 안에 NotificationPageResponse 가 들어간다고 가정
                    .andExpect(jsonPath("$.data.notifications[0].notificationId").value(1L))
                    .andExpect(jsonPath("$.data.notifications[0].notificationTitle").value("알림1"))
                    .andExpect(jsonPath("$.data.notifications[0].read").value(false))
                    .andExpect(jsonPath("$.data.notifications[1].notificationId").value(2L))
                    .andExpect(jsonPath("$.data.notifications[1].read").value(true))
                    .andExpect(jsonPath("$.data.pagination.currentPage").value(0))
                    .andExpect(jsonPath("$.data.pagination.totalPages").value(1))
                    .andExpect(jsonPath("$.data.pagination.totalItems").value(2));

            then(queryService).should()
                    .getUserNotifications(eq(userId), eq(page), eq(size));
        } finally {
            clearAuth();
        }
    }

    @Test
    @DisplayName("GET /api/users/{userId}/notifications/unread-count - 본인 요청 시 미읽음 카운트가 ApiResponse 로 반환된다")
    void getUnreadCountSuccess() throws Exception {
        // given
        Long userId = 200L;
        long unread = 3L;

        given(queryService.getUnreadCount(userId))
                .willReturn(unread);

        // 본인 인증 세팅
        setAuth(userAuth(userId));

        try {
            // when & then
            mockMvc.perform(get("/api/users/{userId}/notifications/unread-count", userId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    // ApiResponse<Long> 의 data 에 숫자만 들어간다고 가정
                    .andExpect(jsonPath("$.data").value((int) unread)); // long -> int 캐스팅
        } finally {
            clearAuth();
        }
    }

    @Test
    @DisplayName("GET /api/users/{userId}/notifications/stream - SSE 구독 요청 시 SseEmitter 반환 및 emitters.add(userId) 호출")
    void stream_success() throws Exception {
        // given
        Long userId = 300L;
        SseEmitter emitter = new SseEmitter();

        given(emitters.add(userId)).willReturn(emitter);

        // (현재 stream() 에는 인증 체크가 없지만, 나중 확장을 고려해서 본인 인증을 넣어도 됨)
        setAuth(userAuth(userId));

        try {
            mockMvc.perform(get("/api/users/{userId}/notifications/stream", userId)
                            .accept(MediaType.TEXT_EVENT_STREAM))
                    .andExpect(status().isOk())
                    // SseEmitter 는 비동기 처리이므로 asyncStarted 여부를 확인
                    .andExpect(request().asyncStarted());

            then(emitters).should().add(userId);
        } finally {
            clearAuth();
        }
    }

    @Test
    @DisplayName("GET /api/admin/notifications/templates - ADMIN 권한으로 템플릿 목록/검색 시 ApiResponse 로 반환된다")
    void getTemplatesSuccess() throws Exception {
        // given
        String keyword = "공지";
        int page = 0;
        int size = 10;

        NotificationTemplateSummaryResponse t1 = NotificationTemplateSummaryResponse.builder()
                .templateId(1L)
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("공지 템플릿")
                .templateBody("내용입니다")
                .createdAt(LocalDateTime.now())
                .build();

        Pagination pagination = Pagination.builder()
                .currentPage(page)
                .totalPages(1)
                .totalItems(1L)
                .build();

        NotificationTemplatePageResponse pageResponse = NotificationTemplatePageResponse.builder()
                .templates(List.of(t1))
                .pagination(pagination)
                .build();

        given(queryService.getTemplates(eq(keyword), eq(page), eq(size)))
                .willReturn(pageResponse);

        // ADMIN 인증 세팅 (@PreAuthorize("hasRole('ADMIN')") 대응)
        setAuth(adminAuth());

        try {
            // when & then
            mockMvc.perform(get("/api/admin/notifications/templates")
                            .param("keyword", keyword)
                            .param("page", String.valueOf(page))
                            .param("size", String.valueOf(size))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    // data.templates[0] 내용 검증
                    .andExpect(jsonPath("$.data.templates[0].templateId").value(1L))
                    .andExpect(jsonPath("$.data.templates[0].templateKind").value("NORMAL"))
                    .andExpect(jsonPath("$.data.templates[0].templateTitle").value("공지 템플릿"))
                    .andExpect(jsonPath("$.data.templates[0].templateBody").value("내용입니다"))
                    // pagination 검증
                    .andExpect(jsonPath("$.data.pagination.currentPage").value(page))
                    .andExpect(jsonPath("$.data.pagination.totalPages").value(1))
                    .andExpect(jsonPath("$.data.pagination.totalItems").value(1));

            then(queryService).should()
                    .getTemplates(eq(keyword), eq(page), eq(size));
        } finally {
            clearAuth();
        }
    }

    // ===== SecurityContext 헬퍼 =====

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
