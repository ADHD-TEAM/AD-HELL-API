package com.adhd.ad_hell.domain.reward.command.domain.repository;

import com.adhd.ad_hell.domain.reward.command.domain.aggregate.Reward;
import java.util.Optional;

public interface RewardRepository {

  Reward save(Reward reward);

  Optional<Reward> findById(Long rewardId);

  void deleteById(Long rewardId);
}
