package com.adhd.ad_hell.domain.advertise.command.infrastructure.repository;

import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface JpaAdRepository extends AdRepository, JpaRepository<Ad, Long> {

    @Override
    @Query(value = "SELECT COALESCE(like_count, 0) + COALESCE(bookmark_count, 0) + COALESCE(comment_count, 0)+ COALESCE(view_count, 0)" +
            "FROM ad WHERE ad_id = :adId", nativeQuery = true)
    Integer findTotalScoreById(@Param("adId") Long adId);

    // Top 20 스코어 행 조회 (프로젝션)
    @Query(value =
            "SELECT " +
                    "  ad_id       AS adId, " +
                    "  category_id AS categoryId, " +
                    "  (COALESCE(like_count,0) + COALESCE(bookmark_count,0) + COALESCE(comment_count,0) + COALESCE(view_count,0)) AS score " +
                    "FROM ad " +
                    "ORDER BY score DESC " +
                    "LIMIT 20",
            nativeQuery = true)
    List<AdTopScoreRow> findTop20ByTotalScore();

    interface AdTopScoreRow {
        Long getAdId();
        Long getCategoryId();
        Float getScore();
    }

}
