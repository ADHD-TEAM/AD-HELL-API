package com.adhd.ad_hell.domain.notification.query.service;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationRepository;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationPageResponse;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationSummaryResponse;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceGetUserNotificationsTest {

    @Mock
    JpaNotificationRepository notificationRepo;   // getUnreadCount()에서만 사용

    @Mock
    NotificationMapper mapper;                   // MyBatis Mapper

    @InjectMocks
    NotificationQueryService sut;

    @Test
    @DisplayName("회원 알림 목록 조회 시, MyBatis Mapper를 통해 페이지네이션된 NotificationPageResponse 를 반환한다")
    void getUserNotificationsSuccess() {
        // given
        Long userId = 100L;
        int page = 0;
        Integer size = 10;

        int expectedSize = 10;
        int expectedOffset = 0;   // page * size

        NotificationSummaryResponse r1 = NotificationSummaryResponse.builder()
                .notificationId(1L)
                .notificationTitle("알림1")
                .notificationBody("내용1")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        NotificationSummaryResponse r2 = NotificationSummaryResponse.builder()
                .notificationId(2L)
                .notificationTitle("알림2")
                .notificationBody("내용2")
                .read(true)
                .createdAt(LocalDateTime.now())
                .build();

        List<NotificationSummaryResponse> summaries = List.of(r1, r2);
        long totalItems = 2L;

        // mapper 스텁
        when(mapper.selectUserNotifications(eq(userId), eq(expectedOffset), eq(expectedSize)))
                .thenReturn(summaries);
        when(mapper.countUserNotifications(eq(userId)))
                .thenReturn(totalItems);

        // when
        NotificationPageResponse response = sut.getUserNotifications(userId, page, size);

        // then
        // 1) mapper 호출 검증
        verify(mapper, times(1))
                .selectUserNotifications(eq(userId), eq(expectedOffset), eq(expectedSize));
        verify(mapper, times(1))
                .countUserNotifications(eq(userId));
        verifyNoMoreInteractions(mapper);

        // 2) 응답 내용 검증
        assertNotNull(response);
        assertNotNull(response.getNotifications());
        assertEquals(2, response.getNotifications().size());

        NotificationSummaryResponse first = response.getNotifications().get(0);
        assertEquals("알림1", first.getNotificationTitle());
        assertEquals("내용1", first.getNotificationBody());
        assertFalse(first.isRead());

        // pagination 확인
        assertNotNull(response.getPagination());
        Pagination p = response.getPagination();
        assertEquals(0, p.getCurrentPage());
        assertEquals(1, p.getTotalPages());   // 2개 / size=10 → 1페이지
        assertEquals(2, p.getTotalItems());
    }

    @Test
    @DisplayName("미읽음 카운트 조회 시 YnType.N 기준 count 를 반환한다")
    void getUnreadCountSuccess() {
        // given
        Long userId = 200L;
        long unreadCount = 5L;

        when(notificationRepo.countByUserIdAndReadYn(userId, YnType.N))
                .thenReturn(unreadCount);

        // when
        long result = sut.getUnreadCount(userId);

        // then
        assertEquals(unreadCount, result);
        verify(notificationRepo, times(1))
                .countByUserIdAndReadYn(userId, YnType.N);
        verifyNoMoreInteractions(notificationRepo);
    }

    @Test
    @DisplayName("size 가 0 이하이면 DEFAULT_PAGE_SIZE(10)을 사용한다")
    void getUserNotificationsSizeZeroUsesDefault() {
        // given
        Long userId = 100L;
        int page = 0;
        Integer size = 0;   // 0 또는 -1 같은 값

        int expectedSize = 10;
        int expectedOffset = 0;

        NotificationSummaryResponse r1 = NotificationSummaryResponse.builder()
                .notificationId(1L)
                .notificationTitle("알림1")
                .notificationBody("내용1")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        List<NotificationSummaryResponse> summaries = List.of(r1);
        long totalItems = 1L;

        when(mapper.selectUserNotifications(eq(userId), eq(expectedOffset), eq(expectedSize)))
                .thenReturn(summaries);
        when(mapper.countUserNotifications(eq(userId)))
                .thenReturn(totalItems);

        // when
        NotificationPageResponse response = sut.getUserNotifications(userId, page, size);

        // then
        verify(mapper).selectUserNotifications(eq(userId), eq(expectedOffset), eq(expectedSize));
        verify(mapper).countUserNotifications(eq(userId));

        // pageSize가 10으로 들어갔는지 → mapper 호출로 검증
        Pagination p = response.getPagination();
        assertEquals(0, p.getCurrentPage());
        assertEquals(1, p.getTotalPages());
        assertEquals(totalItems, p.getTotalItems());
    }


}
