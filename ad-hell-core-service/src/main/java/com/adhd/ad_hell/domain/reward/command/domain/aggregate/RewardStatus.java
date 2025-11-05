package com.adhd.ad_hell.domain.reward.command.domain.aggregate;

public enum RewardStatus {
  ACTIVATE,
  DEACTIVATE,
  DELETE;

  public RewardStatus toggle() {
    return this == ACTIVATE ? DEACTIVATE : ACTIVATE;
  }
}
