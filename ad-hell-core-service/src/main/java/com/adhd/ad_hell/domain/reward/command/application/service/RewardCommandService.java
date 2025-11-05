package com.adhd.ad_hell.domain.reward.command.application.service;

import com.adhd.ad_hell.domain.reward.command.application.dto.request.CreateRewardRequest;
import com.adhd.ad_hell.domain.reward.command.application.dto.request.UpdateRewardRequest;

public interface RewardCommandService {

  void createReward(CreateRewardRequest req);
  void updateReward(Long rewardId, UpdateRewardRequest req);
  void toggleStatusReward(Long rewardId);
  void deleteReward(Long rewardId);
}
