package com.adhd.ad_hell.domain.report.query.service;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.report.query.dto.request.ReportSearchRequest;
import com.adhd.ad_hell.domain.report.query.dto.response.ReportDetailResponse;
import com.adhd.ad_hell.domain.report.query.dto.response.ReportListResponse;
import com.adhd.ad_hell.domain.report.query.dto.response.ReportResponse;
import com.adhd.ad_hell.domain.report.query.mapper.ReportMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportQueryService {

  private final ReportMapper reportMapper;
  private final SecurityUtil securityUtil;

  @Transactional(readOnly = true)
  public ReportDetailResponse getReportDetail(Long reportId) {
    ReportDetailResponse dto = reportMapper.findReportById(reportId);
    if (dto == null) {
      throw new BusinessException(ErrorCode.REWARD_NOT_FOUND);
    }
    return dto;
  }

  @Transactional(readOnly = true)
  public ReportListResponse getReportList(ReportSearchRequest request) {
    List<ReportResponse> reportResponses = reportMapper.findReportList(request);
    long totalItems = reportMapper.countReports(request);

    int page = request.getPage();
    int size = request.getSize();

    return ReportListResponse.builder()
                             .reports(reportResponses)
                             .pagination(Pagination.builder()
                                                   .currentPage(page)
                                                   .totalPages((int) Math.ceil((double) reportResponses.size() / size))
                                                   .totalItems(totalItems)
                                                   .build())
                             .build();
  }

  @Transactional(readOnly = true)
  public ReportListResponse getMyReports(ReportSearchRequest request) {
    Long userId = securityUtil.getLoginUserInfo().getUserId();
    request.setUserId(userId);

    List<ReportResponse> reportResponses = reportMapper.findReportsByUserId(request);
    long totalItems = reportMapper.countMyReports(request);

    int page = request.getPage();
    int size = request.getSize();

    return ReportListResponse.builder()
                             .reports(reportResponses)
                             .pagination(Pagination.builder()
                                            .currentPage(page)
                                            .totalPages((int) Math.ceil((double) reportResponses.size() / size))
                                            .totalItems(totalItems)
                                            .build())
                             .build();
  }

  @Transactional(readOnly = true)
  public ReportDetailResponse getMyReportDetail(Long reportId) {
    Long userId = securityUtil.getLoginUserInfo().getUserId();
    ReportDetailResponse detail = reportMapper.findReportById(reportId);
    if (detail == null)
      throw new BusinessException(ErrorCode.REPORT_NOT_FOUND);

    if (!detail.getReporterId().equals(userId)) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }

    return detail;
  }
}
