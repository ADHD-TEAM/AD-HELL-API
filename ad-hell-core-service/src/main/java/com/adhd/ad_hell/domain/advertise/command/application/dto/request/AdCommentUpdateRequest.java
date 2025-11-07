package com.adhd.ad_hell.domain.advertise.command.application.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdCommentUpdateRequest {
    private final Long adId;
    private final Long userId;
    private final String content;
}
