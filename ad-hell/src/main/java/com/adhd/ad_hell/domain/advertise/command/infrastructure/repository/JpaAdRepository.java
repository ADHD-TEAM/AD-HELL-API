package com.adhd.ad_hell.domain.advertise.command.infrastructure.repository;

import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface JpaAdRepository extends AdRepository, JpaRepository<Ad, Long> {

    @Override
    @Query(value = "SELECT COALESCE(like_count, 0) + COALESCE(bookmark_count, 0) + COALESCE(comment_count, 0) " +
            "FROM ad WHERE ad_id = :adId", nativeQuery = true)
    Integer findTotalScoreById(@Param("adId") Long adId);
}
