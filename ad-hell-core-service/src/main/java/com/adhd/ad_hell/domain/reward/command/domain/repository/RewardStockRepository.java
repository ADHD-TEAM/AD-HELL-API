package com.adhd.ad_hell.domain.reward.command.domain.repository;

import com.adhd.ad_hell.domain.reward.command.domain.aggregate.Reward;
import com.adhd.ad_hell.domain.reward.command.domain.aggregate.RewardStock;
import com.adhd.ad_hell.domain.reward.command.domain.aggregate.RewardStockStatus;
import java.util.Optional;

public interface RewardStockRepository {

  RewardStock save(RewardStock rewardStock);

  Optional<RewardStock> findFirstByRewardAndStatusOrderByCreatedAtAsc(Reward reward, RewardStockStatus status);
}
