package com.adhd.ad_hell.domain.ad_comment.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdCommentDetailResponse {
    private final AdCommentDto adComment;
}
