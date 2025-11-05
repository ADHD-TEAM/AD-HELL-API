package com.adhd.ad_hell.domain.report.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.report.command.application.dto.request.CreateReportRequest;
import com.adhd.ad_hell.domain.report.command.application.service.ReportCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
@Tag(name = "Report Command", description = "신고 등록 API")
public class ReportCommandController {

  private final ReportCommandService reportCommandService;

  @Operation(
      summary = "특정 항목 신고하기",
      description = "광고, 게시글 등 다양한 항목들을 신고한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "201",
          description = "신고 성공"
      ),
  })
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> createReport(
      @RequestBody CreateReportRequest req
  ) {
    reportCommandService.createReport(req);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success(null));
  }

}
