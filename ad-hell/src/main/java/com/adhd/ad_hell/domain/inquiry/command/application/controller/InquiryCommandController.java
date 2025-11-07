package com.adhd.ad_hell.domain.inquiry.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.inquiry.command.application.dto.request.InquiryAnswerRequest;
import com.adhd.ad_hell.domain.inquiry.command.application.dto.request.InquiryCreateRequest;
import com.adhd.ad_hell.domain.inquiry.command.application.service.InquiryCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inquiries")
@Tag(name = "Inquiry Command", description = "문의 등록 및 답변 관리API")
public class InquiryCommandController {

    private final InquiryCommandService inquiryCommandService;



    @Operation(
            summary = "문의 등록",
            description = "사용자가 새로운 문의를 등록한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "문의 등록 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 유효하지 않음"
            ),
    })
    // 문의 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createInquiry(@RequestBody InquiryCreateRequest req) {
        inquiryCommandService.createInquiry(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }


    @Operation(
            summary = "문의 답변 등록/수정 (관리자용)",
            description = "관리자가 특정 문의에 대한 답변을 등록하거나 수정한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "문의 답변 등록/수정 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "해당 문의를 찾을 수 없음"
            ),
    })
    // 관리 : 문의 답변 등록, 수정
    @PatchMapping("/admin/{id}/answer")
    public ResponseEntity<ApiResponse<Void>> answerInquiry(@PathVariable("id") Long inquiryId,
                                                           @Valid @RequestBody InquiryAnswerRequest req) {
        inquiryCommandService.answerInquiry(inquiryId, req);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
