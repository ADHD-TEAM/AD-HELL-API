package com.adhd.ad_hell.domain.ad_favorite.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.ad_favorite.command.application.dto.request.AdFavoriteCreateRequest;
import com.adhd.ad_hell.domain.ad_favorite.command.application.dto.response.AdFavoriteCommandResponse;
import com.adhd.ad_hell.domain.ad_favorite.command.application.service.AdFavoriteCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ad_favorites")
@RequiredArgsConstructor
public class AdFavoriteCommandController {

    private final AdFavoriteCommandService service;

    /** 즐겨찾기 등록 */
    @PostMapping
    public ResponseEntity<ApiResponse<AdFavoriteCommandResponse>> AdFavoriteCreate(
            @RequestBody AdFavoriteCreateRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.AdFavoriteCreate(req)));
    }

    /** 즐겨찾기 삭제 */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> AdFavoriteDelete(
            @RequestParam Long userId,
            @RequestParam Long adId
    ) {
        service.AdFavoriteDelete(userId, adId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
