package com.adhd.ad_hell.domain.ad_favorite.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.request.AdFavoriteSearchRequest;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.response.AdFavoriteDTO;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.response.AdFavoriteListResponse;
import com.adhd.ad_hell.domain.ad_favorite.query.service.AdFavoriteQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ad-favorites")
public class AdFavoriteQueryController {

    private final AdFavoriteQueryService adFavoriteQueryService;

    /** 내 즐겨찾기 목록 (페이징 포함) */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<AdFavoriteListResponse>> getMyFavorites(
            @ModelAttribute AdFavoriteSearchRequest req
    ) {
        AdFavoriteListResponse response = adFavoriteQueryService.getMyFavorite(req);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 즐겨찾기 단건 상세 */
    @GetMapping("/{favoriteId}")
    public ResponseEntity<ApiResponse<AdFavoriteDTO>> getFavoriteDetail(
            @PathVariable Long favoriteId
    ) {
        AdFavoriteDTO response = adFavoriteQueryService.getFavoriteDetail(favoriteId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 특정 광고 즐겨찾기 존재 여부 */
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> existsFavorite(
            @RequestParam Long userId,
            @RequestParam Long adId
    ) {
        boolean exists = adFavoriteQueryService.existsFavorite(userId, adId);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}
