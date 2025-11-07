package com.adhd.ad_hell.domain.ad_favorite.query.dto.response;

import com.adhd.ad_hell.common.dto.Pagination;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdFavoriteListResponse {
    private final List<AdFavoriteDTO> adFavorites; // 도메인 이름 반영
    private final Pagination pagination;              // 페이징 메타
}
