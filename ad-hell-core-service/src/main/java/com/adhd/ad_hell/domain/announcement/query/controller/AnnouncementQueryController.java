package com.adhd.ad_hell.domain.announcement.query.controller;

import com.adhd.ad_hell.domain.announcement.query.dto.request.AnnouncementSearchRequest;
import com.adhd.ad_hell.domain.announcement.query.dto.response.AnnouncementDetailResponse;
import com.adhd.ad_hell.domain.announcement.query.dto.response.AnnouncementListResponse;
import com.adhd.ad_hell.domain.announcement.query.service.AnnouncementQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcement Query", description = "공지사항 조회 API")
public class AnnouncementQueryController {

    private final AnnouncementQueryService announcementQueryService;

    @Operation(
            summary = "공지사항 목록 조회",
            description = "검색/페이징 조건으로 공지사항 목록을 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "공지사항 목록 조회 성공"
            ),
    })
    @GetMapping
    public ResponseEntity<AnnouncementListResponse> getAnnouncements(AnnouncementSearchRequest request) {
        // 예: /api/announcements?page=1&size=20&keyword=test&status=Y
        return ResponseEntity.ok(announcementQueryService.getAnnouncements(request));
    }

    @Operation(
            summary = "공지사항 상세 조회",
            description = "공지사항 ID로 상세 정보를 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "공지사항 상세 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "해당 공지사항을 찾을 수 없음"
            ),
    })
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDetailResponse> getAnnouncementDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(announcementQueryService.getAnnouncementDetail(id));
    }
}
