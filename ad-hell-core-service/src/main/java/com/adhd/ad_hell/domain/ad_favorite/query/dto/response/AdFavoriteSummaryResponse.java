package com.adhd.ad_hell.domain.ad_favorite.query.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdFavoriteSummaryResponse {

    private final Long id;
    private final Long adId;
    private final String adTitle;
    private final String adName;
    private final LocalDateTime createdAt;

    public static AdFavoriteSummaryResponse from(AdFavoriteDTO dto) {
        return AdFavoriteSummaryResponse.builder()
                .id(dto.getId())
                .adId(dto.getAdId())
                .adTitle(dto.getAdTitle())
                .adName(dto.getAdName())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
