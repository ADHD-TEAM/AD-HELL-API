package com.adhd.ad_hell.domain.ad_favorite.command.application.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdFavoriteCommandResponse {
    private Long favId;
    private Long userId;
    private Long adId;
    private LocalDateTime createdAt;
}
