package com.adhd.ad_hell.domain.report.query.dto.response;

import com.adhd.ad_hell.domain.report.command.domain.aggregate.ReportStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportDetailResponse {
  private final Long id;
  private final Long categoryId;
  private final Long targetId;
  private final Long adminId;
  private final Long reporterId;
  private final ReportStatus status;
  private final String reasonDetail;
  private final LocalDateTime createdAt;
}
