package com.adhd.ad_hell.domain.reward.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.adhd.ad_hell.domain.reward.command.domain.aggregate.RewardStockStatus;
import com.adhd.ad_hell.domain.reward.query.dto.response.RewardStockResponse;
import com.adhd.ad_hell.domain.reward.query.mapper.RewardStockMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RewardStockQueryServiceTest {

  @Mock
  private RewardStockMapper rewardStockMapper;

  @InjectMocks
  private RewardStockQueryService rewardStockQueryService;

  @Test
  @DisplayName("특정 리워드의 재고 목록을 정상적으로 조회한다")
  void getRewardStockList_success() {
    // given
    Long rewardId = 1L;
    List<RewardStockResponse> mockList = List.of(
        new RewardStockResponse("PIN-001", LocalDateTime.now().plusDays(3), RewardStockStatus.ACTIVATE),
        new RewardStockResponse("PIN-002",  LocalDateTime.now().plusDays(3), RewardStockStatus.USED)
    );
    when(rewardStockMapper.findRewardStocks(rewardId)).thenReturn(mockList);

    // when
    List<RewardStockResponse> result = rewardStockQueryService.getRewardStockList(rewardId);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getPinNumber()).isEqualTo("PIN-001");
    verify(rewardStockMapper, times(1)).findRewardStocks(rewardId);
  }
}
