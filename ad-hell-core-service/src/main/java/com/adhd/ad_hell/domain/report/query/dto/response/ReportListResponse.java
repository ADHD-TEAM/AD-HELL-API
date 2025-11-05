package com.adhd.ad_hell.domain.report.query.dto.response;

import com.adhd.ad_hell.common.dto.Pagination;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportListResponse {
  private final List<ReportResponse> reports;
  private final Pagination pagination;
}
