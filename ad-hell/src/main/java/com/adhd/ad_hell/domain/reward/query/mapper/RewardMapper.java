package com.adhd.ad_hell.domain.reward.query.mapper;

import com.adhd.ad_hell.domain.reward.query.dto.RewardDto;
import com.adhd.ad_hell.domain.reward.query.dto.request.RewardSearchRequest;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RewardMapper {
  RewardDto findRewardById(@Param("rewardId") Long rewardId);
  List<RewardDto> findRewards(RewardSearchRequest request);
  long countRewards(RewardSearchRequest request);
}
