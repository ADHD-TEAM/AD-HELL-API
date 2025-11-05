package com.adhd.ad_hell.domain.reward.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.reward.query.dto.request.RewardSearchRequest;
import com.adhd.ad_hell.domain.reward.query.dto.response.RewardDetailResponse;
import com.adhd.ad_hell.domain.reward.query.dto.response.RewardListResponse;
import com.adhd.ad_hell.domain.reward.query.dto.response.RewardStockResponse;
import com.adhd.ad_hell.domain.reward.query.service.RewardQueryService;
import com.adhd.ad_hell.domain.reward.query.service.RewardStockQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rewards")
@Tag(name = "Reward Command", description = "경품 목록 API")
public class RewardQueryController {

  private final RewardQueryService rewardQueryService;
  private final RewardStockQueryService rewardStockQueryService;

  @Operation(
      summary = "경품 상세 내역",
      description = "특정 경품의 상세 내역을 조회한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "조회 성공"
      ),
  })
  @GetMapping("/{rewardId}")
  public ResponseEntity<ApiResponse<RewardDetailResponse>> getRewardDetails(
      @PathVariable Long rewardId
  ) {
    RewardDetailResponse response = rewardQueryService.getRewardDetail(rewardId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(
      summary = "경품 리스트",
      description = "경품 리스트를 조회한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "조회 성공"
      ),
  })
  @GetMapping
  public ResponseEntity<ApiResponse<RewardListResponse>> getRewards(
      RewardSearchRequest request
  ) {
    RewardListResponse list = rewardQueryService.getRewardList(request);
    return ResponseEntity.ok(ApiResponse.success(list));
  }

  @Operation(
      summary = "경품 재고 리스트 조회",
      description = "경품 재고 리스트를 조회한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "조회 성공"
      ),
  })
  @GetMapping("/{rewardId}/stock")
  public ResponseEntity<ApiResponse<List<RewardStockResponse>>> getRewardStocks(
      @PathVariable Long rewardId
  ) {
    List<RewardStockResponse> list = rewardStockQueryService.getRewardStockList(rewardId);
    return ResponseEntity.ok(ApiResponse.success(list));
  }
}
