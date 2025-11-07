package com.adhd.ad_hell.domain.report.query.dto.response;

import com.adhd.ad_hell.domain.report.command.domain.aggregate.ReportStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportResponse {
  private final Long id;
  private final Long categoryId;
  private final Long reporterId;
  private final ReportStatus status;
  private final LocalDateTime createdAt;
}
