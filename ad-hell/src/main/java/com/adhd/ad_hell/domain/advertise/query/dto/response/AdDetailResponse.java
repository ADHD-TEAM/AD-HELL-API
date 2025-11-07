package com.adhd.ad_hell.domain.advertise.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdDetailResponse {
    private final AdDto ad;
}
