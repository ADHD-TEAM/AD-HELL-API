package com.adhd.ad_hell.domain.user.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.user.command.dto.request.UserPointRequest;
import com.adhd.ad_hell.domain.user.command.dto.response.UserPointResponse;
import com.adhd.ad_hell.domain.user.query.dto.response.UserPointHistoryResponse;
import com.adhd.ad_hell.domain.user.query.service.UserPointQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Point Command", description = "포인트 이력 API")
public class UserPointQueryController {

  private final UserPointQueryService userPointQueryService;

  @Operation(
      summary = "내 포인트 이력 조회",
      description = "내 포인트 전체 이력을 조회한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "조회 성공"
      ),
  })
  @GetMapping("/point")
  public ResponseEntity<ApiResponse<List<UserPointHistoryResponse>>> getMyPointHistory() {
    List<UserPointHistoryResponse> response = userPointQueryService.getMyPointHistory();
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
