package com.adhd.ad_hell.domain.ranking.query.service;

import com.adhd.ad_hell.domain.ranking.command.domain.aggregate.AdRank;
import com.adhd.ad_hell.domain.ranking.query.mapper.AdRankingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdRankingQueryService {

    private final AdRankingMapper adRankingMapper;

    @Transactional(readOnly = true)
    public List<AdRank> getTop20ByScore() {
        return adRankingMapper.selectTop20ByScore();
    }
}
