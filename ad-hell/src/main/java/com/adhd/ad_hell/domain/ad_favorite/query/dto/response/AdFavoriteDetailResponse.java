package com.adhd.ad_hell.domain.ad_favorite.query.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 즐겨찾기 상세 조회 응답 DTO
 * - Summary보다 추가 정보(userId, imageUrl, updatedAt 등) 포함
 */
@Getter
@Builder
public class AdFavoriteDetailResponse {

    private final Long id;
    private final Long userId;
    private final Long adId;
    private final String adTitle;
    private final String adName;

    public static AdFavoriteDetailResponse from(AdFavoriteDTO dto) {
        return AdFavoriteDetailResponse.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .adId(dto.getAdId())
                .adTitle(dto.getAdTitle())
                .adName(dto.getAdName())
                .build();
    }
}
