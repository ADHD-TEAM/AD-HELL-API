package com.adhd.ad_hell.domain.advertise.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.advertise.query.dto.request.AdSearchRequest;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdDetailResponse;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdListResponse;
import com.adhd.ad_hell.domain.advertise.query.service.AdQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ads")
@Tag(name = "Ad Query", description = "광고 조회 API")
public class AdQueryController {

    private final AdQueryService adQueryService;

    @GetMapping("/{adId}")
    public ResponseEntity<ApiResponse<AdDetailResponse>> getAd(
            @PathVariable Long adId
    ) {
        AdDetailResponse response = adQueryService.getAd(adId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<AdListResponse>> getAds(
            AdSearchRequest AdSearchRequest
    ) {
        AdListResponse response = adQueryService.getAds(AdSearchRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}