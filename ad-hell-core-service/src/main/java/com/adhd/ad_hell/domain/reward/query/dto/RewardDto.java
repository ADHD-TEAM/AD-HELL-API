package com.adhd.ad_hell.domain.reward.query.dto;

import com.adhd.ad_hell.domain.reward.command.domain.aggregate.RewardStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RewardDto {
  private Long id;
  private String name;
  private String description;
  private Integer pointCost;
  private Integer stock;
  private RewardStatus status;

  private Long categoryId;
  private String categoryName;
  private Long parentCategoryId;
  private String parentCategoryName;
}
