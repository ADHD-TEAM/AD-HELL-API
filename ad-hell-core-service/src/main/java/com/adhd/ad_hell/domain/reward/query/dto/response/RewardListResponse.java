package com.adhd.ad_hell.domain.reward.query.dto.response;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.reward.query.dto.RewardDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RewardListResponse {
  private final List<RewardDto> rewards;
  private final Pagination pagination;
}
