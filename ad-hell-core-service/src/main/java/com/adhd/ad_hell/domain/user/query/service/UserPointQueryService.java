package com.adhd.ad_hell.domain.user.query.service;

import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.user.command.entity.PointStatus;
import com.adhd.ad_hell.domain.user.query.dto.response.UserPointHistoryResponse;
import com.adhd.ad_hell.domain.user.query.mapper.PointHistoryMapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPointQueryService {

  private final PointHistoryMapper pointHistoryMapper;
  private final SecurityUtil securityUtil;

  @Transactional(readOnly = true)
  public List<UserPointHistoryResponse> getMyPointHistory() {
    Long userId = securityUtil.getLoginUserInfo().getUserId();

    List<UserPointHistoryResponse> userPointHistoryResponses = pointHistoryMapper.findMyPointHistory(userId, PointStatus.VALID);
    return userPointHistoryResponses;
  }
}
