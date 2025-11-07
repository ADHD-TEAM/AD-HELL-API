package com.adhd.ad_hell.domain.ad_favorite.command.domain.repository;

import com.adhd.ad_hell.domain.ad_favorite.command.domain.aggregate.AdFavorite;
import java.util.Optional;

public interface AdFavoriteRepository {
    AdFavorite save(AdFavorite adFavorite);
    Optional<AdFavorite> findById(Long id);
    boolean exists(Long userId, Long adId);
    void delete(Long userId, Long adId);
}
