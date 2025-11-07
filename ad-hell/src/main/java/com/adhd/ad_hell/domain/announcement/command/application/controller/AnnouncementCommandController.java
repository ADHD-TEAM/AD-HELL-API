package com.adhd.ad_hell.domain.announcement.command.application.controller;

import com.adhd.ad_hell.domain.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.adhd.ad_hell.domain.announcement.command.application.dto.request.AnnouncementUpdateRequest;
import com.adhd.ad_hell.domain.announcement.command.application.dto.response.AnnouncementCommandResponse;
import com.adhd.ad_hell.domain.announcement.command.application.service.AnnouncementCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcement Command", description = "공지사항 등록·수정·삭제 API")
public class AnnouncementCommandController {

    private final AnnouncementCommandService announcementService;

    @Operation(
            summary = "공지사항 등록",
            description = "관리자가 새로운 공지사항을 등록한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "공지사항 등록 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터"
            ),
    })
    @PostMapping
    public ResponseEntity<AnnouncementCommandResponse> create(@RequestBody AnnouncementCreateRequest request) {
        AnnouncementCommandResponse response = announcementService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "공지사항 수정",
            description = "기존 공지사항의 제목, 내용, 상태를 수정한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "공지사항 수정 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "공지사항을 찾을 수 없음"
            ),
    })
    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementCommandResponse> update(
            @PathVariable Long id,
            @RequestBody AnnouncementUpdateRequest request
    ) {
        AnnouncementCommandResponse response = announcementService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "공지사항 삭제",
            description = "특정 공지사항을 삭제한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "공지사항 삭제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "공지사항을 찾을 수 없음"
            ),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
