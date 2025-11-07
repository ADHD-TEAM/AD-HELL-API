package com.adhd.ad_hell.domain.reward.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.reward.command.application.dto.request.CreateRewardRequest;
import com.adhd.ad_hell.domain.reward.command.application.dto.request.CreateRewardStockRequest;
import com.adhd.ad_hell.domain.reward.command.application.dto.request.UpdateRewardRequest;
import com.adhd.ad_hell.domain.reward.command.application.service.RewardCommandService;
import com.adhd.ad_hell.domain.reward.command.application.service.RewardStockCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rewards")
@Tag(name = "Reward Command", description = "경품 관리 API")
public class RewardCommandController {

  private final RewardCommandService rewardCommandService;
  private final RewardStockCommandService rewardStockCommandService;


  /* 기본 반환 전부 void 처리, 추후 변경 필요 */
  @Operation(
      summary = "경품 등록",
      description = "경품을 등록한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "201",
          description = "등록 성공"
      ),
  })
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> createReward(@RequestBody CreateRewardRequest req) {
    rewardCommandService.createReward(req);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success(null));
  }

  @Operation(
      summary = "경품 수정",
      description = "경품을 수정한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "수정 성공"
      ),
  })
  @PutMapping("/{rewardId}")
  public ResponseEntity<ApiResponse<Void>> updateReward(@PathVariable Long rewardId, @RequestBody UpdateRewardRequest req) {
    rewardCommandService.updateReward(rewardId, req);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(
      summary = "경품 상태 변경",
      description = "경품을 상태를 변경한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "상태 변경 성공"
      ),
  })
  @PatchMapping("/{rewardId}/status")
  public ResponseEntity<ApiResponse<Void>> toggleStatusReward(@PathVariable Long rewardId) {
    rewardCommandService.toggleStatusReward(rewardId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(
      summary = "경품 삭제",
      description = "경품을 삭제한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "삭제 성공"
      ),
  })
  @DeleteMapping("/{rewardId}")
  public ResponseEntity<ApiResponse<Void>> deleteReward(@PathVariable Long rewardId) {
    rewardCommandService.deleteReward(rewardId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(
      summary = "경품 재고 추가",
      description = "경품 재고를 추가한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "201",
          description = "추가 성공"
      ),
  })
  @PostMapping("/{rewardId}/stocks")
  public ResponseEntity<ApiResponse<Void>> createRewardStock(
      @PathVariable Long rewardId,
      @RequestBody CreateRewardStockRequest req
  ) {
    rewardStockCommandService.createRewardStock(rewardId, req);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success(null));
  }

  @Operation(
      summary = "경품 교환",
      description = "기존 포인트를 차감하고 경품 코드를 이메일로 발송한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "교환 성공"
      ),
  })
  @PostMapping("/{rewardId}/exchange")
  public ResponseEntity<ApiResponse<Void>> sendReward(@PathVariable Long rewardId) {
    rewardStockCommandService.sendReward(rewardId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
