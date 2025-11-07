package com.adhd.ad_hell.domain.ranking.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.ranking.command.domain.aggregate.AdRank;
import com.adhd.ad_hell.domain.ranking.query.service.AdRankingQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ad-rankings")
public class AdRankingQueryController {

    private final AdRankingQueryService adRankingQueryService;

    @GetMapping("/top20")
    public ResponseEntity<ApiResponse<List<AdRank>>> getTop20ByScore() {
        List<AdRank> rankings = adRankingQueryService.getTop20ByScore();
        return ResponseEntity.ok(ApiResponse.success(rankings));
    }
}
