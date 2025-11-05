package com.adhd.ad_hell.domain.reward.query.dto.request;

import com.adhd.ad_hell.domain.report.command.domain.aggregate.ReportStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RewardSearchRequest {

  private Integer page = 1;
  private Integer size = 20;
  private Long categoryId;
  private String name;

  public int getPage() {
    if (page == null || page < 1) {
      return 1;
    }
    return page;
  }

  public int getSize() {
    if (size == null || size < 1) {
      return 10;
    }

    if (size > 100) {
      return 100;
    }
    return size;
  }

  public int getOffset() {
    return (getPage() - 1) * getSize();
  }

  public int getLimit() {
    return getSize();
  }
}
