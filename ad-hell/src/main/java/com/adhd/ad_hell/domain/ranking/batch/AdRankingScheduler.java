package com.adhd.ad_hell.domain.ranking.batch;

import com.adhd.ad_hell.domain.advertise.command.infrastructure.repository.JpaAdRepository;
import com.adhd.ad_hell.domain.ranking.command.domain.aggregate.AdRank;
import com.adhd.ad_hell.domain.ranking.command.domain.repository.AdRankingRepository;
import com.adhd.ad_hell.domain.ranking.command.infrastructure.repository.JpaAdRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdRankingScheduler {

    private final JpaAdRepository adRepository;
    private final AdRankingRepository adRankingRepository;

    // 매시간 정각 실행: 초 분 시 일 월 요일
    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void recalculateTop20AdRanksHourly() {
        // 1) Top 20 점수 계산
        List<JpaAdRepository.AdTopScoreRow> topRows = adRepository.findTop20ByTotalScore();

        // 2) 기존 랭킹 초기화(전체 삭제 후 재생성)
        adRankingRepository.deleteAllInBatch();

        // 3) 새 랭킹 저장
        topRows.forEach(row -> {
            AdRank rank = AdRank.builder()
                    .adId(row.getAdId())
                    .categoryId(row.getCategoryId())
                    .score(row.getScore() == null ? 0F : row.getScore())
                    .build();
            adRankingRepository.save(rank);
        });
    }
}
