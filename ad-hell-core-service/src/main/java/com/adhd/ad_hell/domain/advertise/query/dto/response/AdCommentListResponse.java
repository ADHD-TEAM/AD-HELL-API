package com.adhd.ad_hell.domain.advertise.query.dto.response;

import com.adhd.ad_hell.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AdCommentListResponse {
    private final List<AdCommentDto> adComments;
    private final Pagination pagination;
}
