package com.adhd.ad_hell.domain.advertise.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdComment;
import com.adhd.ad_hell.domain.advertise.query.dto.request.AdCommentSearchRequest;
import com.adhd.ad_hell.domain.advertise.query.dto.request.AdSearchRequest;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdCommentDetailResponse;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdCommentListResponse;
import com.adhd.ad_hell.domain.advertise.query.service.AdCommentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ad")
public class AdCommandQueryController {

    private final AdCommentQueryService adCommentQueryService;

    @GetMapping("/Comment/{adCommentId}")
    public ResponseEntity<ApiResponse<AdCommentDetailResponse>> getAdComment(
            @PathVariable Long adCommentId
    ) {
        AdCommentDetailResponse response = adCommentQueryService.getComment(adCommentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/Comment")
    public ResponseEntity<ApiResponse<AdCommentListResponse>> getAdsComment(
            AdCommentSearchRequest AdCommentSearchRequest
    ) {
        AdCommentListResponse response = adCommentQueryService.getComments(AdCommentSearchRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/Comment/My")
    public ResponseEntity<ApiResponse<AdCommentListResponse>> getMyComments(
            AdCommentSearchRequest AdCommentSearchRequest
    ) {
        AdCommentListResponse response = adCommentQueryService.getMyComments(AdCommentSearchRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
