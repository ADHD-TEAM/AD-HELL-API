package com.adhd.ad_hell.domain.ranking.command.infrastructure.repository;

import com.adhd.ad_hell.domain.ranking.command.domain.aggregate.AdRank;
import com.adhd.ad_hell.domain.ranking.command.domain.repository.AdRankingRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAdRankingRepository extends AdRankingRepository , JpaRepository<AdRank, Long> {
}
