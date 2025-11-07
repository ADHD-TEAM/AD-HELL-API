package com.adhd.ad_hell.domain.reward.query.mapper;

import com.adhd.ad_hell.domain.reward.query.dto.response.RewardStockResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RewardStockMapper {
  List<RewardStockResponse> findRewardStocks(@Param("rewardId") Long rewardId);
}
