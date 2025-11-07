package com.adhd.ad_hell.domain.ranking.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.ranking.command.application.dto.request.AdRankingCreateRequest;
import com.adhd.ad_hell.domain.ranking.command.application.service.AdRankingCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ad-rankings")
public class AdRankingCommandController {

    private final AdRankingCommandService adRankingCommandService;

    /* 광고 랭킹 등록 */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createAdRanking(
            @Valid @RequestBody AdRankingCreateRequest req
    ) {
        adRankingCommandService.createAdRanking(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }

}
