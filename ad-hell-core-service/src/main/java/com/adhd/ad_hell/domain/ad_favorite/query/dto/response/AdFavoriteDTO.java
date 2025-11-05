package com.adhd.ad_hell.domain.ad_favorite.query.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
public class AdFavoriteDTO {

    private Long id;
    private Long userId;
    private Long adId;
    private String adTitle;
    private String adName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
