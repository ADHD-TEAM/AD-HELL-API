package com.adhd.ad_hell.domain.reward.command.application.dto.request;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateRewardStockRequest {
  private final String pinNumber;
  private final LocalDateTime expiredAt;
}
