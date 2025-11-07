package com.adhd.ad_hell.domain.ad_favorite.query.mapper;

import com.adhd.ad_hell.domain.ad_favorite.query.dto.request.AdFavoriteSearchRequest;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.response.AdFavoriteDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdFavoriteMapper {

    /** 내 즐겨찾기 목록 (페이징/검색/정렬) */
    List<AdFavoriteDTO> findMyFavorites(@Param("req") AdFavoriteSearchRequest req);

    /** 내 즐겨찾기 총 건수 (페이징 계산용) */
    long countMyFavorites(@Param("req") AdFavoriteSearchRequest req);

    /** 즐겨찾기 단건 상세 */
    AdFavoriteDTO findFavoriteById(@Param("favoriteId") Long favoriteId);

    /** 특정 광고가 사용자의 즐겨찾기에 존재하는지 여부 */
    boolean existsFavorite(@Param("userId") Long userId, @Param("adId") Long adId);
}
