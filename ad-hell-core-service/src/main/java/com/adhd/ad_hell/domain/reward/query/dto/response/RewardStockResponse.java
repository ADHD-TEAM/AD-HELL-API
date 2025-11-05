package com.adhd.ad_hell.domain.reward.query.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RewardStockResponse {
  private final String pinNumber;
  private final LocalDateTime expiredAt;
}
