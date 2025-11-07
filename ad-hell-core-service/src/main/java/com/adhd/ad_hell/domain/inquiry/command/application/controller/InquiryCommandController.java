package com.adhd.ad_hell.domain.inquiry.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.inquiry.command.application.dto.request.InquiryAnswerRequest;
import com.adhd.ad_hell.domain.inquiry.command.application.dto.request.InquiryCreateRequest;
import com.adhd.ad_hell.domain.inquiry.command.application.service.InquiryCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
public class InquiryCommandController {

    private final InquiryCommandService inquiryCommandService;

    // 문의 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createInquiry(@RequestBody InquiryCreateRequest req) {
        inquiryCommandService.createInquiry(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }

    // 관리 : 문의 답변 등록, 수정
    @PatchMapping("/admin/{id}/answer")
    public ResponseEntity<ApiResponse<Void>> answerInquiry(@PathVariable("id") Long inquiryId,
                                                           @Valid @RequestBody InquiryAnswerRequest req) {
        inquiryCommandService.answerInquiry(inquiryId, req);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
