package com.adhd.ad_hell.domain.advertise.command.application.dto.request;

import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdStatus;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.FileType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdCreateRequest {
    Long adId;
    Long userId;
    Long categoryId;
    String title;
    AdStatus Status;
    int like_count;
    int bookmark_count;
    int comment_count;
    int view_count;
}
