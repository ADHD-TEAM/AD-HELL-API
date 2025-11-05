package com.adhd.ad_hell.domain.reward.command.infrastructure.repository;

import com.adhd.ad_hell.domain.reward.command.domain.aggregate.Reward;
import com.adhd.ad_hell.domain.reward.command.domain.repository.RewardRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRewardRepository extends RewardRepository, JpaRepository<Reward, Long> {

}
