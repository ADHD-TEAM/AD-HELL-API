package com.adhd.ad_hell.domain.ranking.command.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdRankingCreateRequest {

    @NotNull
    private Long categoryId;

    @NotNull
    private Long adId;

    @NotNull
    private float score;
    // score는 요청에서 받지 않습니다. 서비스에서 ad 테이블을 조회하여 계산해야하지만
    // 추후 구현으로 넘기고, 현상황에서 req 에서 받아서 구현하는것으로 마무리

}
