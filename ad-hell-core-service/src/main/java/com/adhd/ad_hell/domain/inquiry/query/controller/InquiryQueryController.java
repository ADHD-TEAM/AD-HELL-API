package com.adhd.ad_hell.domain.inquiry.query.controller;

import com.adhd.ad_hell.domain.inquiry.query.dto.request.InquirySearchRequest;
import com.adhd.ad_hell.domain.inquiry.query.dto.response.InquiryDetailResponse;
import com.adhd.ad_hell.domain.inquiry.query.dto.response.InquiryListResponse;
import com.adhd.ad_hell.domain.inquiry.query.service.InquiryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryQueryController {

    private final InquiryQueryService inquiryQueryService;

    /** 회원 - 내 문의 목록 */
    @GetMapping("/my")
    public ResponseEntity<InquiryListResponse> getMyInquiries(InquirySearchRequest req) {
        // 예: /api/inquiries/my?userId=1&page=1&size=20&keyword=...&answered=Y
        return ResponseEntity.ok(inquiryQueryService.getMyInquiries(req));
    }

    /** 회원 - 내 문의 상세 (userId 스코프 필요) */
    @GetMapping("/my/{id}")
    public ResponseEntity<InquiryDetailResponse> getMyInquiry(@PathVariable Long id,
                                                              @RequestParam Long userId) {
        return ResponseEntity.ok(inquiryQueryService.getMyInquiryById(userId, id));
    }

    /** 관리자 - 전체 목록 */
    @GetMapping("/admin")
    public ResponseEntity<InquiryListResponse> getAdminInquiries(InquirySearchRequest req) {
        // 예: /api/inquiries/admin?page=1&size=20&keyword=...&answered=N
        return ResponseEntity.ok(inquiryQueryService.getAdminInquiries(req));
    }

    /** 관리자 - 상세 */
    @GetMapping("/admin/{id}")
    public ResponseEntity<InquiryDetailResponse> getAdminInquiry(@PathVariable Long id) {
        return ResponseEntity.ok(inquiryQueryService.getAdminInquiryById(id));
    }
}
