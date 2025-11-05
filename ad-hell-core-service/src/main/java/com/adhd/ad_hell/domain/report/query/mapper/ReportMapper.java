package com.adhd.ad_hell.domain.report.query.mapper;

import com.adhd.ad_hell.domain.report.query.dto.request.ReportSearchRequest;
import com.adhd.ad_hell.domain.report.query.dto.response.ReportDetailResponse;
import com.adhd.ad_hell.domain.report.query.dto.response.ReportListResponse;
import com.adhd.ad_hell.domain.report.query.dto.response.ReportResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportMapper {

  ReportDetailResponse findReportById(Long reportId);

  List<ReportResponse> findReportList(ReportSearchRequest request);

  List<ReportResponse> findReportsByUserId(ReportSearchRequest request);

  long countReports(ReportSearchRequest request);
  long countMyReports(ReportSearchRequest request);
}
