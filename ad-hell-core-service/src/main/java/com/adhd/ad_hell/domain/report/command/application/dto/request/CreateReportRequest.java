package com.adhd.ad_hell.domain.report.command.application.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateReportRequest {
  private Long categoryId;
  private Long targetId;
  private String reasonDetail;
}
