package com.adhd.ad_hell.domain.ad_favorite.command.application.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdFavoriteCreateRequest {
    private Long userId;
    private Long adId;
}
