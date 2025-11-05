package com.adhd.ad_hell.domain.user.command.service;

import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.user.command.dto.request.UserPointRequest;
import com.adhd.ad_hell.domain.user.command.dto.response.UserPointResponse;
import com.adhd.ad_hell.domain.user.command.entity.PointHistory;
import com.adhd.ad_hell.domain.user.command.entity.PointType;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.PointCommandRepository;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointCommandServiceImpl implements PointCommandService {

  private final UserCommandRepository userCommandRepository;
  private final PointCommandRepository pointCommandRepository;
  private final SecurityUtil securityUtil;

  @Transactional
  @Override
  public UserPointResponse earnPoints(UserPointRequest request) {

    Long userId = securityUtil.getLoginUserInfo().getUserId();

    User user = userCommandRepository.findByUserId(userId)
                                     .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    user.earnPoint(request.getPoint());

    PointHistory history = PointHistory.builder()
                                       .user(user)
                                       .changeAmount(request.getPoint())
                                       .balance(user.getAmount())
                                       .type(PointType.VIEW)
                                       .description("포인트 적립 (광고 시청)")
                                       .build();

    pointCommandRepository.save(history);

    return UserPointResponse.builder()
                            .amount(user.getAmount())
                            .message("포인트가 적립되었습니다.")
                            .build();
  }
}
