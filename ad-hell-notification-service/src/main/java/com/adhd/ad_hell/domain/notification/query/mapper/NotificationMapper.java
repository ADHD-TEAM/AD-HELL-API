package com.adhd.ad_hell.domain.notification.query.mapper;

import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationSummaryResponse;
import com.adhd.ad_hell.domain.notification.query.dto.response.NotificationTemplateSummaryResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    /* ---------- Notification(알림 목록 조회) ---------- */

    /**
     * 특정 유저의 알림 목록 조회 (페이징)
     *
     * @param userId 대상 유저 ID
     * @param offset 시작 인덱스 (page * size)
     * @param size   페이지 크기
     */
    List<NotificationSummaryResponse> selectUserNotifications(
            @Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 특정 유저의 알림 전체 개수
     */
    long countUserNotifications(@Param("userId") Long userId);


    /* ---------- Template(템플릿 목록 조회) ---------- */

    /**
     * 템플릿 목록/검색 (삭제되지 않은 것만, 제목 keyword 검색)
     *
     * @param keyword 검색 키워드 (null/blank 이면 전체)
     * @param offset  시작 인덱스
     * @param size    페이지 크기
     */
    List<NotificationTemplateSummaryResponse> selectTemplates(
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 템플릿 총 개수 (검색 조건 포함)
     */
    long countTemplates(@Param("keyword") String keyword);
}
