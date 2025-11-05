package com.adhd.ad_hell.domain.reward.command.application.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateRewardRequest {
  private final Long categoryId;
  private final String name;
  private final String description;
  private final Integer pointCost;
  private final Integer stock;
}
