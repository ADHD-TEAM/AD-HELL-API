package com.adhd.ad_hell.domain.reward.query.service;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.reward.query.dto.request.RewardSearchRequest;
import com.adhd.ad_hell.domain.reward.query.dto.response.RewardDetailResponse;
import com.adhd.ad_hell.domain.reward.query.dto.RewardDto;
import com.adhd.ad_hell.domain.reward.query.dto.response.RewardListResponse;
import com.adhd.ad_hell.domain.reward.query.mapper.RewardMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RewardQueryService {

  private final RewardMapper rewardMapper;

  @Transactional(readOnly = true)
  public RewardDetailResponse getRewardDetail(Long rewardId) {
    RewardDto dto = rewardMapper.findRewardById(rewardId);
    if (dto == null) {
      throw new BusinessException(ErrorCode.REWARD_NOT_FOUND);
    }
    return RewardDetailResponse.from(dto);
  }

  @Transactional(readOnly = true)
  public RewardListResponse getRewardList(RewardSearchRequest request) {
    List<RewardDto> rewardDtos = rewardMapper.findRewards(request);
    long totalItems = rewardMapper.countRewards(request);

    int page = request.getPage();
    int size = request.getSize();

    return RewardListResponse.builder()
                             .rewards(rewardDtos)
                             .pagination(Pagination.builder()
                                                   .currentPage(page)
                                                   .totalPages((int) Math.ceil((double) rewardDtos.size() / size))
                                                   .totalItems(totalItems)
                                                   .build())
                             .build();
  }
}
