package com.adhd.ad_hell.domain.report.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.report.query.dto.request.ReportSearchRequest;
import com.adhd.ad_hell.domain.report.query.dto.response.ReportDetailResponse;
import com.adhd.ad_hell.domain.report.query.dto.response.ReportListResponse;
import com.adhd.ad_hell.domain.report.query.dto.response.ReportResponse;
import com.adhd.ad_hell.domain.report.query.service.ReportQueryService;
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
@RequestMapping("/api/reports")
@Tag(name = "Report Command", description = "신고 내역 API")
public class ReportQueryController {

  private final ReportQueryService reportQueryService;

  @Operation(
      summary = "신고 항목 상세내역 조회",
      description = "특정 신고 항목의 상세내역을 확인한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "조회 성공"
      ),
  })
  @GetMapping("/{reportId}")
  public ResponseEntity<ApiResponse<ReportDetailResponse>> getReportDetails(
      @PathVariable Long reportId
  ) {
    ReportDetailResponse response = reportQueryService.getReportDetail(reportId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(
      summary = "신고 내역 조회",
      description = "신고 내역을 확인한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "조회 성공"
      ),
  })
  @GetMapping
  public ResponseEntity<ApiResponse<ReportListResponse>> getReports(
      ReportSearchRequest request
  ) {
    ReportListResponse list = reportQueryService.getReportList(request);
    return ResponseEntity.ok(ApiResponse.success(list));
  }

  @Operation(
      summary = "나의 신고 내역 조회",
      description = "나의 신고 내역을 확인한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "조회 성공"
      ),
  })
  @GetMapping("/me")
  public ResponseEntity<ApiResponse<ReportListResponse>> getMyReports(
      ReportSearchRequest request
  ) {
    ReportListResponse list = reportQueryService.getMyReports(request);
    return ResponseEntity.ok(ApiResponse.success(list));
  }

  @Operation(
      summary = "나의 신고 상세내역 조회",
      description = "나의 신고 상세내역을 확인한다."
  )
  @ApiResponses({
      @io.swagger.v3.oas.annotations.responses.ApiResponse(
          responseCode = "200",
          description = "조회 성공"
      ),
  })
  @GetMapping("/me/{reportId}")
  public ResponseEntity<ApiResponse<ReportDetailResponse>> getMyReportDetail(
      @PathVariable Long reportId
  ) {
    ReportDetailResponse detail = reportQueryService.getMyReportDetail(reportId);
    return ResponseEntity.ok(ApiResponse.success(detail));
  }
}
