package com.adhd.ad_hell.domain.notification.query.service;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationTemplateKind;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationRepository;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationTemplatePageResponse;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationTemplateSummaryResponse;
import com.adhd.ad_hell.domain.notification.query.mapper.NotificationMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceGetTemplatesTest {

    @Mock
    JpaNotificationRepository notificationRepo;   // getUnreadCount()용, 여기선 안 씀

    @Mock
    NotificationMapper mapper;                    // MyBatis Mapper

    @InjectMocks
    NotificationQueryService sut;                 // System Under Test

    @DisplayName("검색어 없으면 keyword=null, offset=0, size=10 으로 MyBatis 쿼리를 호출하고 Pagination을 계산한다")
    @Test
    void noKeyWordTest() {
        // given
        String keyword = null;
        int page = 0;
        Integer size = null; // DEFAULT_PAGE_SIZE(10) 기대

        int expectedSize = 10;
        int expectedOffset = 0;

        // MyBatis에서 돌려줄 템플릿 요약 목록 더미
        NotificationTemplateSummaryResponse s1 =
                NotificationTemplateSummaryResponse.builder()
                        .templateId(1L)
                        .templateKind(NotificationTemplateKind.NORMAL)
                        .templateTitle("공지1")
                        .templateBody("내용1")
                        .createdAt(LocalDateTime.now())
                        .build();

        List<NotificationTemplateSummaryResponse> summaries = List.of(s1);
        long totalItems = 1L;   // 전체 개수도 1개라고 가정

        // mapper 스텁
        when(mapper.selectTemplates(isNull(), eq(expectedOffset), eq(expectedSize)))
                .thenReturn(summaries);
        when(mapper.countTemplates(isNull()))
                .thenReturn(totalItems);

        // when
        NotificationTemplatePageResponse result =
                sut.getTemplates(keyword, page, size);

        // then
        // 1) mapper 호출 검증
        verify(mapper, times(1))
                .selectTemplates(isNull(), eq(expectedOffset), eq(expectedSize));
        verify(mapper, times(1))
                .countTemplates(isNull());

        // 2) 결과 검증
        assertNotNull(result);
        assertNotNull(result.getTemplates());
        assertNotNull(result.getPagination());

        // 템플릿 검증
        assertEquals(1, result.getTemplates().size());
        assertEquals("공지1", result.getTemplates().get(0).getTemplateTitle());
        assertEquals(NotificationTemplateKind.NORMAL, result.getTemplates().get(0).getTemplateKind());

        // Pagination 검증
        Pagination p = result.getPagination();
        assertEquals(0, p.getCurrentPage());
        assertEquals(1, p.getTotalPages());
        assertEquals(totalItems, p.getTotalItems());
    }

    @DisplayName("검색어가 있으면 keyword, offset, size를 그대로 MyBatis 쿼리에 전달하고 Pagination을 계산한다")
    @Test
    void KeyWordTest() {
        // given
        String keyword = "이벤트";
        int page = 0;
        Integer size = 10;   // 명시적으로 10

        int expectedSize = 10;
        int expectedOffset = 0;

        NotificationTemplateSummaryResponse s1 =
                NotificationTemplateSummaryResponse.builder()
                        .templateId(2L)
                        .templateKind(NotificationTemplateKind.EVENT)
                        .templateTitle("이벤트 공지")
                        .templateBody("이벤트 내용")
                        .createdAt(LocalDateTime.now())
                        .build();

        List<NotificationTemplateSummaryResponse> summaries = List.of(s1);
        long totalItems = 1L;

        // mapper 스텁
        when(mapper.selectTemplates(eq(keyword), eq(expectedOffset), eq(expectedSize)))
                .thenReturn(summaries);
        when(mapper.countTemplates(eq(keyword)))
                .thenReturn(totalItems);

        // when
        NotificationTemplatePageResponse result =
                sut.getTemplates(keyword, page, size);

        // then
        verify(mapper, times(1))
                .selectTemplates(eq(keyword), eq(expectedOffset), eq(expectedSize));
        verify(mapper, times(1))
                .countTemplates(eq(keyword));

        assertNotNull(result);
        assertNotNull(result.getTemplates());
        assertNotNull(result.getPagination());

        // 템플릿 검증
        assertEquals(1, result.getTemplates().size());
        assertEquals("이벤트 공지", result.getTemplates().get(0).getTemplateTitle());
        assertEquals(NotificationTemplateKind.EVENT, result.getTemplates().get(0).getTemplateKind());

        // Pagination 검증
        Pagination p = result.getPagination();
        assertEquals(0, p.getCurrentPage());
        assertEquals(1, p.getTotalPages());
        assertEquals(totalItems, p.getTotalItems());
    }
}
