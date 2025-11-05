package com.adhd.ad_hell.domain.user.query.dto.response;

import com.adhd.ad_hell.domain.user.command.entity.PointType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPointHistoryResponse {
  private Long id;
  private Integer changeAmount;
  private Integer balance;
  private String description;
  private PointType type;
  private LocalDateTime createdAt;
}
