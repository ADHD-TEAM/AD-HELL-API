package com.adhd.ad_hell.domain.reward.command.infrastructure.repository;

import com.adhd.ad_hell.domain.reward.command.domain.aggregate.RewardStock;
import com.adhd.ad_hell.domain.reward.command.domain.repository.RewardStockRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRewardStockRepository extends RewardStockRepository, JpaRepository<RewardStock, Long> {

}
