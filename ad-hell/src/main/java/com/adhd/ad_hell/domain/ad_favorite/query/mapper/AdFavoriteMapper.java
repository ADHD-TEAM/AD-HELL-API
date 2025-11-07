package com.adhd.ad_hell.domain.ad_favorite.query.mapper;

import com.adhd.ad_hell.domain.ad_favorite.query.dto.request.AdFavoriteSearchRequest;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.response.AdFavoriteDTO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdFavoriteMapper {

    /** 내 즐겨찾기 목록 조회 (페이징/검색 포함) */
    List<AdFavoriteDTO> findMyFavorites(AdFavoriteSearchRequest req);

    /** 내 즐겨찾기 총 개수 */
    long countMyFavorites(AdFavoriteSearchRequest req);

    /** 즐겨찾기 단건 상세 조회 */
    AdFavoriteDTO findFavoriteById(Long favoriteId);

    /** 즐겨찾기 존재 여부 */
    boolean existsFavorite(@Param("userId") Long userId,
                           @Param("adId") Long adId);
}
