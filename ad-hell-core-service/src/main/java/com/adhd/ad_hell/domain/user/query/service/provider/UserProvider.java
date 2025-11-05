package com.adhd.ad_hell.domain.user.query.service.provider;

import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.query.dto.response.UserRewardInfo;

public interface UserProvider {
  User getUserById(Long userId);
  UserRewardInfo decreaseUserPoint(Long userId, Integer point);
  void compensateUserPoint(Long userId, Long targetHistoryId);
}
