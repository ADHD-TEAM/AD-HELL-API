package com.adhd.ad_hell.domain.ad_comment.command.application.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdCommentCreateRequest {
    private final Long adId;
    private final Long userId;
    private final String content;
}
