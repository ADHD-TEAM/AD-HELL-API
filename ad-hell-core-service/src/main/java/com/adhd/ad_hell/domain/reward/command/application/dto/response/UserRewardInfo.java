package com.adhd.ad_hell.domain.reward.command.application.dto.response;

public record UserRewardInfo(
    String email,
    String name,
    Long pointHistoryId
) {}
