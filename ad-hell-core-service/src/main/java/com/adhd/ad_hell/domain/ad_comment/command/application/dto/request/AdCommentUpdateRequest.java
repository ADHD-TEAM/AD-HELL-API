package com.adhd.ad_hell.domain.ad_comment.command.application.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdCommentUpdateRequest {
    private final String content;
}
