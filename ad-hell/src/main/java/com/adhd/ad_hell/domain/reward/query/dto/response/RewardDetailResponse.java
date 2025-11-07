package com.adhd.ad_hell.domain.reward.query.dto.response;

import com.adhd.ad_hell.common.dto.CategoryInfoResponse;
import com.adhd.ad_hell.domain.reward.command.domain.aggregate.RewardStatus;
import com.adhd.ad_hell.domain.reward.query.dto.RewardDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RewardDetailResponse {
  private final Long id;
  private final String name;
  private final String description;
  private final Integer pointCost;
  private final Integer stock;
  private final RewardStatus status;
  private CategoryInfoResponse category;

  public static RewardDetailResponse from(RewardDto dto) {
    return RewardDetailResponse.builder()
                               .id(dto.getId())
                               .name(dto.getName())
                               .description(dto.getDescription())
                               .pointCost(dto.getPointCost())
                               .stock(dto.getStock())
                               .status(dto.getStatus())
                               .category(CategoryInfoResponse.builder()
                                                             .categoryId(dto.getCategoryId())
                                                             .categoryName(dto.getCategoryName())
                                                             .parentId(dto.getParentCategoryId())
                                                             .parentName(dto.getParentCategoryName())
                                                             .build())
                               .build();
  }
}
