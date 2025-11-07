package com.adhd.ad_hell.domain.reward.query.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.reward.command.domain.aggregate.RewardStatus;
import com.adhd.ad_hell.domain.reward.query.dto.RewardDto;
import com.adhd.ad_hell.domain.reward.query.dto.request.RewardSearchRequest;
import com.adhd.ad_hell.domain.reward.query.dto.response.RewardDetailResponse;
import com.adhd.ad_hell.domain.reward.query.dto.response.RewardListResponse;
import com.adhd.ad_hell.domain.reward.query.mapper.RewardMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RewardQueryServiceTest {

  @Mock
  private RewardMapper rewardMapper;

  @InjectMocks
  private RewardQueryService rewardQueryService;

  @Nested
  @DisplayName("getRewardDetail() 테스트")
  class GetRewardDetailTests {

    @Test
    @DisplayName("리워드 상세 조회 성공")
    void getRewardDetail_success() {
      // given
      Long rewardId = 1L;
      RewardDto mockDto = new RewardDto();
      mockDto.setId(rewardId);
      mockDto.setName("스타벅스 아메리카노");
      mockDto.setDescription("기프티콘");
      mockDto.setPointCost(100);
      mockDto.setStock(50);
      mockDto.setStatus(RewardStatus.ACTIVATE);
      mockDto.setCategoryId(10L);
      mockDto.setCategoryName("음료");
      mockDto.setParentCategoryId(1L);
      mockDto.setParentCategoryName("식음료");


      when(rewardMapper.findRewardById(rewardId)).thenReturn(mockDto);

      // when
      RewardDetailResponse result = rewardQueryService.getRewardDetail(rewardId);

      // then
      assertThat(result.getName()).isEqualTo("스타벅스 아메리카노");
      assertThat(result.getName()).isEqualTo("스타벅스 아메리카노");
      verify(rewardMapper).findRewardById(rewardId);
    }

    @Test
    @DisplayName("리워드가 존재하지 않으면 예외 발생")
    void getRewardDetail_notFound() {
      // given
      Long rewardId = 999L;
      when(rewardMapper.findRewardById(rewardId)).thenReturn(null);

      // when & then
      assertThatThrownBy(() -> rewardQueryService.getRewardDetail(rewardId))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(ErrorCode.REWARD_NOT_FOUND.getMessage());
    }
  }

  @Nested
  @DisplayName("getRewardList() 테스트")
  class GetRewardListTests {

    @Test
    @DisplayName("리워드 목록 조회 성공")
    void getRewardList_success() {
      // given
      RewardSearchRequest req = new RewardSearchRequest();
      req.setPage(1);
      req.setSize(10);

      RewardDto dto1 = new RewardDto();
      dto1.setId(1L);
      dto1.setName("커피 쿠폰");
      dto1.setDescription("설명1");
      dto1.setPointCost(50);
      dto1.setStock(100);
      dto1.setStatus(RewardStatus.ACTIVATE);
      dto1.setCategoryId(10L);
      dto1.setCategoryName("음료");

      RewardDto dto2 = new RewardDto();
      dto2.setId(2L);
      dto2.setName("버거 쿠폰");
      dto2.setDescription("설명2");
      dto2.setPointCost(70);
      dto2.setStock(30);
      dto2.setStatus(RewardStatus.ACTIVATE);
      dto2.setCategoryId(20L);
      dto2.setCategoryName("식사류");
      List<RewardDto> rewards = List.of(dto1, dto2);

      when(rewardMapper.findRewards(req)).thenReturn(rewards);
      when(rewardMapper.countRewards(req)).thenReturn(2L);

      // when
      RewardListResponse result = rewardQueryService.getRewardList(req);

      // then
      assertThat(result.getRewards()).hasSize(2);
      assertThat(result.getRewards().get(0).getName()).isEqualTo("커피 쿠폰");
      assertThat(result.getPagination().getTotalItems()).isEqualTo(2L);

      verify(rewardMapper).findRewards(req);
      verify(rewardMapper).countRewards(req);
    }
  }
}
