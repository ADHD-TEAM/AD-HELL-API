package com.adhd.ad_hell.domain.ad_favorite.query.service;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.request.AdFavoriteSearchRequest;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.response.AdFavoriteDTO;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.response.AdFavoriteListResponse;
import com.adhd.ad_hell.domain.ad_favorite.query.mapper.AdFavoriteMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdFavoriteQueryService {

    private final AdFavoriteMapper adFavoriteMapper;

    /** 내 즐겨찾기 목록 (페이징 포함, adFavorites 필드 사용) */
    public AdFavoriteListResponse getMyFavorite(AdFavoriteSearchRequest req) {
        // 목록 (매퍼 반환 타입에 맞춰 DTO 사용)
        List<AdFavoriteDTO> adFavorites = adFavoriteMapper.findMyFavorites(req);

        // 전체 건수
        long totalItems = adFavoriteMapper.countMyFavorites(req);

        // 페이지 계산 (req 내부에서 이미 null/범위 보정)
        int page = req.getPage();
        int size = req.getSize();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        Pagination pagination = Pagination.builder()
                .currentPage(page)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .build();

        return AdFavoriteListResponse.builder()
                .adFavorites(adFavorites)
                .pagination(pagination)
                .build();
    }

    /** 즐겨찾기 단건 상세 */
    public AdFavoriteDTO getFavoriteDetail(Long favoriteId) {
        return adFavoriteMapper.findFavoriteById(favoriteId);
    }
    /** 특정 광고가 사용자의 즐겨찾기에 존재하는지 여부 */
    public boolean existsFavorite(Long userId, Long adId) {
        return adFavoriteMapper.existsFavorite(userId, adId);
    }
}
