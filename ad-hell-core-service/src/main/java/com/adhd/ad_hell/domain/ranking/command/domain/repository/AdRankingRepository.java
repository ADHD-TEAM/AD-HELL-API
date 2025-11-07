package com.adhd.ad_hell.domain.ranking.command.domain.repository;

import com.adhd.ad_hell.domain.ranking.command.domain.aggregate.AdRank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface AdRankingRepository {
    AdRank save(AdRank adRank);
    Optional<AdRank> findById(Long rankingId);

}
