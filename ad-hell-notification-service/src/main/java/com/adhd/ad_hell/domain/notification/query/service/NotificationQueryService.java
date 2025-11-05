package com.adhd.ad_hell.domain.notification.query.service;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import com.adhd.ad_hell.domain.notification.command.infrastructure.repository.JpaNotificationRepository;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationPageResponse;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationSummaryResponse;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationTemplatePageResponse;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationTemplateSummaryResponse;
import com.adhd.ad_hell.domain.notification.query.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NotificationQueryService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    // 읽기용 MyBatis Mapper
    private final NotificationMapper mapper;

    // 미읽음 카운트는 JPA 그대로 써도 됨 (원하면 이것도 MyBatis로 뺄 수 있음)
    private final JpaNotificationRepository notificationRepo;

    // 유저 알림 목록 (MyBatis 조회)
    public NotificationPageResponse getUserNotifications(Long userId, int page, Integer size) {
        int pageSize = resolveSize(size);
        int currentPage = Math.max(page, 0);
        int offset = currentPage * pageSize;

        // 1) 목록 조회
        List<NotificationSummaryResponse> items =
                mapper.selectUserNotifications(userId, offset, pageSize);

        // 2) 전체 개수 조회
        long totalItems = mapper.countUserNotifications(userId);

        // 3) totalPages 계산
        int totalPages = (int) ((totalItems + pageSize - 1) / pageSize);

        Pagination pagination = Pagination.builder()
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .build();

        return NotificationPageResponse.builder()
                .notifications(items)
                .pagination(pagination)
                .build();
    }

    // 미읽음 카운트는 JPA 사용
    public long getUnreadCount(Long userId) {
        return notificationRepo.countByUserIdAndReadYn(userId, YnType.N);
    }

    // 관리자 템플릿 목록/검색 (MyBatis 조회)
    public NotificationTemplatePageResponse getTemplates(String keyword, int page, Integer size) {
        int pageSize = resolveSize(size);
        int currentPage = Math.max(page, 0);
        int offset = currentPage * pageSize;

        // 1) 목록 조회
        List<NotificationTemplateSummaryResponse> items =
                mapper.selectTemplates(keyword, offset, pageSize);

        // 2) 전체 개수 조회
        long totalItems = mapper.countTemplates(keyword);

        int totalPages = (int) ((totalItems + pageSize - 1) / pageSize);

        Pagination pagination = Pagination.builder()
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .build();

        return NotificationTemplatePageResponse.builder()
                .templates(items)
                .pagination(pagination)
                .build();
    }

    private int resolveSize(Integer size) {
        return (size == null || size <= 0) ? DEFAULT_PAGE_SIZE : size;
    }
}
