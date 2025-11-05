package com.adhd.ad_hell.domain.ad_favorite.command.infrastructure.repository;

import com.adhd.ad_hell.domain.ad_favorite.command.domain.aggregate.AdFavorite;
import com.adhd.ad_hell.domain.ad_favorite.command.domain.repository.AdFavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AdFavoriteRepositoryImpl implements AdFavoriteRepository {

    private final JpaAdFavoriteRepository jpaRepository;

    @Override
    public Optional<AdFavorite> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean exists(Long userId, Long adId) {
        return jpaRepository.existsByUser_UserIdAndAd_AdId(userId, adId);
    }

    @Override
    public void delete(Long userId, Long adId) {
        jpaRepository.deleteByUser_UserIdAndAd_AdId(userId, adId);
    }

    // save는 CrudRepository에서 이미 상속받음 → 굳이 override X
    public AdFavorite save(AdFavorite adFavorite) {
        return jpaRepository.save(adFavorite);
    }
}
