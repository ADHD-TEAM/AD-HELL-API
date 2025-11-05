package com.adhd.ad_hell.domain.user.query.dto.response;

public record UserRewardInfo(
    String email,
    String name,
    Long pointHistoryId
) {}
