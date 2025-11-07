package com.adhd.ad_hell.domain.ad_favorite.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.ad_favorite.command.application.dto.request.AdFavoriteCreateRequest;
import com.adhd.ad_hell.domain.ad_favorite.command.application.dto.response.AdFavoriteCommandResponse;
import com.adhd.ad_hell.domain.ad_favorite.command.application.service.AdFavoriteCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ad_favorites")
@RequiredArgsConstructor
@Tag(name = "Ad Favorite Command", description = "광고 즐겨찾기 등록 및 삭제 API")
public class AdFavoriteCommandController {

    private final AdFavoriteCommandService service;

    @Operation(
            summary = "즐겨찾기 등록",
            description = "사용자가 특정 광고를 즐겨찾기에 추가한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "즐겨찾기 등록 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터"
            ),
    })
    @PostMapping
    public ResponseEntity<ApiResponse<AdFavoriteCommandResponse>> AdFavoriteCreate(
            @RequestBody AdFavoriteCreateRequest req
    ) {
        AdFavoriteCommandResponse response = service.AdFavoriteCreate(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }



    @Operation(
            summary = "즐겨찾기 삭제",
            description = "사용자가 등록한 광고 즐겨찾기를 해제한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "즐겨찾기 삭제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "즐겨찾기를 찾을 수 없음"
            ),
    })
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> AdFavoriteDelete(
            @RequestParam Long userId,
            @RequestParam Long adId
    ) {
        service.AdFavoriteDelete(userId, adId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
