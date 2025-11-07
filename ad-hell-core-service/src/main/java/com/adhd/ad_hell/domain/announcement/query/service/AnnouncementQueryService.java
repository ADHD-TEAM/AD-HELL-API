package com.adhd.ad_hell.domain.announcement.query.service;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.announcement.query.dto.request.AnnouncementSearchRequest;
import com.adhd.ad_hell.domain.announcement.query.dto.response.AnnouncementDetailResponse;
import com.adhd.ad_hell.domain.announcement.query.dto.response.AnnouncementListResponse;
import com.adhd.ad_hell.domain.announcement.query.dto.response.AnnouncementSummaryResponse;
import com.adhd.ad_hell.domain.announcement.query.mapper.AnnouncementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementQueryService {

    private final AnnouncementMapper announcementMapper;

    /** 공지사항 목록 (검색 + 페이징) */
    public AnnouncementListResponse getAnnouncements(AnnouncementSearchRequest request) {
        // 1) 목록 조회
        List<AnnouncementSummaryResponse> announcements = announcementMapper.findAllAnnouncements(request);

        // 2) 전체 건수 조회
        long totalItems = announcementMapper.countAllAnnouncements(request);

        // 3) 페이지 계산 (DTO 기본값 보장 가정)
        int page = request.getPage();
        int size = request.getSize();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        // 4) Pagination 객체 생성
        Pagination pagination = Pagination.builder()
                .currentPage(page)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .build();

        // 5) 응답 조립
        return AnnouncementListResponse.builder()
                .announcements(announcements)
                .pagination(pagination)
                .build();
    }

    /** 공지사항 단건 상세 조회 */
    public AnnouncementDetailResponse getAnnouncementDetail(Long announcementId) {
        return announcementMapper.findAnnouncementDetailById(announcementId);
    }
}
