package com.adhd.ad_hell.domain.user.command.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.user.command.dto.request.UserPointRequest;
import com.adhd.ad_hell.domain.user.command.dto.response.UserPointResponse;
import com.adhd.ad_hell.domain.user.command.service.PointCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Point Command", description = "포인트 적립 API")
public class UserPointCommandController {

  private final PointCommandService pointCommandService;

  @Operation(
      summary = "포인트 적립",
      description = "회원의 포인트를 적립한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "적립 성공"
      ),
  })
  @PostMapping("/point")
  public ResponseEntity<ApiResponse<UserPointResponse>> earnPoint(@RequestBody UserPointRequest userPointRequest) {
    UserPointResponse response = pointCommandService.earnPoints(userPointRequest);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
