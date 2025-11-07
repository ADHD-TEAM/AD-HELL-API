package com.adhd.ad_hell.domain.announcement.query.controller;

import com.adhd.ad_hell.domain.announcement.query.dto.request.AnnouncementSearchRequest;
import com.adhd.ad_hell.domain.announcement.query.dto.response.AnnouncementDetailResponse;
import com.adhd.ad_hell.domain.announcement.query.dto.response.AnnouncementListResponse;
import com.adhd.ad_hell.domain.announcement.query.service.AnnouncementQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementQueryController {

    private final AnnouncementQueryService announcementQueryService;

    /** 공지사항 목록 (검색 + 페이징) */
    @GetMapping
    public ResponseEntity<AnnouncementListResponse> getAnnouncements(AnnouncementSearchRequest request) {
        // Spring이 GET 쿼리 파라미터를 DTO 필드명 기준으로 자동 매핑해줌
        // 예: /api/announcements?page=1&size=20&keyword=test&status=Y
        return ResponseEntity.ok(announcementQueryService.getAnnouncements(request));
    }

    /** 공지사항 단건 상세 */
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDetailResponse> getAnnouncementDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(announcementQueryService.getAnnouncementDetail(id));
    }
}
