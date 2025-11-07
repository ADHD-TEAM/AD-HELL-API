package com.adhd.ad_hell.domain.announcement.command.application.controller;

import com.adhd.ad_hell.domain.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.adhd.ad_hell.domain.announcement.command.application.dto.request.AnnouncementUpdateRequest;
import com.adhd.ad_hell.domain.announcement.command.application.dto.response.AnnouncementCommandResponse;
import com.adhd.ad_hell.domain.announcement.command.application.service.AnnouncementCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementCommandController {

    private final AnnouncementCommandService announcementService;


    // AnnouncementCreateRequest DTO로 매핑되어 Service로 전달됨
    @PostMapping
    public ResponseEntity<AnnouncementCommandResponse> create(@RequestBody AnnouncementCreateRequest request) {
        // service의 create()호출 -> DB에 공지사항 등록 수행
        // 결과를 AnnouncementCommandResponse DTO로 받아서 HTTP 200 OK로 응답
        return ResponseEntity.ok(announcementService.create(request));
    }

    // 공지사항 수정 (Update)
    // 경로 변수(PathVariable)로 수정할 공지사항 ID를 전달받고,
    // 요청 본문(RequestBody)으로 수정할 데이터(title,content, status)를 받음
    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementCommandResponse> update(
            @PathVariable Long id,                              // URL 경로의 공지 ID
            @RequestBody AnnouncementUpdateRequest request      // 수정 요청 데이터
    ) {
        
        // Service의 update() 호출 -> 해당 공지사항 존재 여부 확인 후 수정
        // 수정된 결과를 DTO로 변환하여 HTTP 200으로 응답
        return ResponseEntity.ok(announcementService.update(id, request));
    }

    
    // 공지사항 삭제
    // 삭제할 공지사항 ID를 전달 받아 삭제 처리 수행
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        
        // Service의 delete()호출 -> 존재 여부 확인 후 삭제 실행
        // 별도의 본문 없이 HTTP 204 No Content로 응답
        announcementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
