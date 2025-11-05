package com.adhd.ad_hell.domain.reward.command.application.service;

import com.adhd.ad_hell.domain.reward.command.application.dto.request.CreateRewardStockRequest;

public interface RewardStockCommandService {

  void createRewardStock(Long rewardId, CreateRewardStockRequest req);

  void sendReward(Long rewardId);
}
