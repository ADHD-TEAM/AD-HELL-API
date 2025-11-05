package com.adhd.ad_hell.domain.user.query.service;

import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.user.command.entity.PointStatus;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.domain.user.query.dto.response.UserPointHistoryResponse;
import com.adhd.ad_hell.domain.user.query.mapper.AdminMapper;
import com.adhd.ad_hell.domain.user.query.mapper.PointHistoryMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointQueryService {

  private final PointHistoryMapper pointHistoryMapper;
  private final SecurityUtil securityUtil;

  public List<UserPointHistoryResponse> getMyPointHistory() {
    Long userId = securityUtil.getLoginUserInfo().getUserId();

    List<UserPointHistoryResponse> userPointHistoryResponses = pointHistoryMapper.findMyPointHistory(userId, PointStatus.VALID);
    return userPointHistoryResponses;
  }
}
