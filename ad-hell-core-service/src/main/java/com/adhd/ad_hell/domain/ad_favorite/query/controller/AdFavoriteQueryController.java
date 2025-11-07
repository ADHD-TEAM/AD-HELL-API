package com.adhd.ad_hell.domain.ad_favorite.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.request.AdFavoriteSearchRequest;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.response.AdFavoriteDTO;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.response.AdFavoriteListResponse;
import com.adhd.ad_hell.domain.ad_favorite.query.service.AdFavoriteQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ad-favorites")
@Tag(name = "Ad Favorite Query", description = "광고 즐겨찾기 조회 API")
public class AdFavoriteQueryController {

    private final AdFavoriteQueryService adFavoriteQueryService;

    @Operation(
            summary = "내 즐겨찾기 목록 조회",
            description = "로그인한 사용자의 즐겨찾기 목록을 페이징하여 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "즐겨찾기 목록 조회 성공"
            ),
    })
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<AdFavoriteListResponse>> getMyFavorites(
            @ModelAttribute AdFavoriteSearchRequest req
    ) {
        AdFavoriteListResponse response = adFavoriteQueryService.getMyFavorite(req);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
            summary = "즐겨찾기 상세 조회",
            description = "즐겨찾기 ID로 특정 즐겨찾기 정보를 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "즐겨찾기 상세 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "해당 즐겨찾기를 찾을 수 없음"
            ),
    })
    @GetMapping("/{favoriteId}")
    public ResponseEntity<ApiResponse<AdFavoriteDTO>> getFavoriteDetail(
            @PathVariable Long favoriteId
    ) {
        AdFavoriteDTO response = adFavoriteQueryService.getFavoriteDetail(favoriteId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
            summary = "즐겨찾기 존재 여부 확인",
            description = "특정 사용자가 특정 광고를 즐겨찾기에 등록했는지 여부를 확인한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "즐겨찾기 존재 여부 확인 성공 (true/false)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 파라미터"
            ),
    })
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> existsFavorite(
            @RequestParam Long userId,
            @RequestParam Long adId
    ) {
        boolean exists = adFavoriteQueryService.existsFavorite(userId, adId);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}
