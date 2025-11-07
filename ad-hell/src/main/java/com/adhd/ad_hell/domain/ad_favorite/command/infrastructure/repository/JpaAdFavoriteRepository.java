package com.adhd.ad_hell.domain.ad_favorite.command.infrastructure.repository;

import com.adhd.ad_hell.domain.ad_favorite.command.domain.aggregate.AdFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAdFavoriteRepository extends JpaRepository<AdFavorite, Long> {
    boolean existsByUser_UserIdAndAd_AdId(Long userId, Long adId);
    void deleteByUser_UserIdAndAd_AdId(Long userId, Long adId);
}
