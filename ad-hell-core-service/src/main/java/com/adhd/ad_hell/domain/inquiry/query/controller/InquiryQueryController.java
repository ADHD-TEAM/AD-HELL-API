package com.adhd.ad_hell.domain.inquiry.query.controller;

import com.adhd.ad_hell.domain.inquiry.query.dto.request.InquirySearchRequest;
import com.adhd.ad_hell.domain.inquiry.query.dto.response.InquiryDetailResponse;
import com.adhd.ad_hell.domain.inquiry.query.dto.response.InquiryListResponse;
import com.adhd.ad_hell.domain.inquiry.query.service.InquiryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
@Tag(name = "Inquiry Query", description = "문의 조회 API (회원/관리자용)")
public class InquiryQueryController {

    private final InquiryQueryService inquiryQueryService;

    @Operation(
            summary = "내 문의 목록 조회 (회원)",
            description = "로그인한 사용자가 자신의 문의 목록을 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "문의 목록 조회 성공"
            ),
    })
    @GetMapping("/my")
    public ResponseEntity<InquiryListResponse> getMyInquiries(InquirySearchRequest req) {
        // 예: /api/inquiries/my?userId=1&page=1&size=20&keyword=...&answered=Y
        return ResponseEntity.ok(inquiryQueryService.getMyInquiries(req));
    }

    @Operation(
            summary = "내 문의 상세 조회 (회원)",
            description = "로그인한 사용자가 자신의 특정 문의 상세 내용을 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "문의 상세 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "해당 문의를 찾을 수 없음"
            ),
    })
    @GetMapping("/my/{id}")
    public ResponseEntity<InquiryDetailResponse> getMyInquiry(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(inquiryQueryService.getMyInquiryById(userId, id));
    }

    @Operation(
            summary = "전체 문의 목록 조회 (관리자)",
            description = "관리자가 등록된 모든 문의 목록을 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "전체 문의 목록 조회 성공"
            ),
    })
    @GetMapping("/admin")
    public ResponseEntity<InquiryListResponse> getAdminInquiries(InquirySearchRequest req) {
        // 예: /api/inquiries/admin?page=1&size=20&keyword=...&answered=N
        return ResponseEntity.ok(inquiryQueryService.getAdminInquiries(req));
    }

    @Operation(
            summary = "문의 상세 조회 (관리자)",
            description = "관리자가 특정 문의의 상세 정보를 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "문의 상세 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "해당 문의를 찾을 수 없음"
            ),
    })
    @GetMapping("/admin/{id}")
    public ResponseEntity<InquiryDetailResponse> getAdminInquiry(@PathVariable Long id) {
        return ResponseEntity.ok(inquiryQueryService.getAdminInquiryById(id));
    }
}
