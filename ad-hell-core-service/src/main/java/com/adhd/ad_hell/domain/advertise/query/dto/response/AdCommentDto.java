package com.adhd.ad_hell.domain.advertise.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdCommentDto {
    private Long adCommentId;
    private Long adId;
    private Long userId;
    private String content;
    private String createdAt;
    private String updateAt;
}
