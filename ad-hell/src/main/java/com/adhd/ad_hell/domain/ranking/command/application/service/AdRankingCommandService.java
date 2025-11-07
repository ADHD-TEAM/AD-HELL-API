package com.adhd.ad_hell.domain.ranking.command.application.service;

import com.adhd.ad_hell.domain.ranking.command.application.dto.request.AdRankingCreateRequest;
import com.adhd.ad_hell.domain.ranking.command.domain.aggregate.AdRank;
import com.adhd.ad_hell.domain.ranking.command.domain.repository.AdRankingRepository;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdRankingCommandService {

    private final AdRankingRepository adRankingRepository;
    private final AdRepository adRepository;

    @Transactional
    public Long createAdRanking(AdRankingCreateRequest request){
//        Integer totalScore = adRepository.findTotalScoreById(request.getAdId());
//        float score = totalScore != null ? totalScore.floatValue() : 0F;

        AdRank adRank = AdRank.builder()
                .categoryId(request.getCategoryId())
                .adId(request.getAdId())
                .score(request.getScore())
                .build();

        AdRank saved = adRankingRepository.save(adRank);
        return saved.getRankId();
    }

}
