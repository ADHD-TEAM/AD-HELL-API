package com.adhd.ad_hell.domain.user.query.service.provider;

import com.adhd.ad_hell.domain.user.command.entity.PointHistory;
import com.adhd.ad_hell.domain.user.command.entity.PointStatus;
import com.adhd.ad_hell.domain.user.command.entity.PointType;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.PointCommandRepository;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.domain.user.query.dto.response.UserRewardInfo;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserProviderImpl implements UserProvider {

  private final UserCommandRepository userCommandRepository;
  private final PointCommandRepository pointCommandRepository;

  @Override
  @Transactional(readOnly = true)
  public User getUserById(Long userId) {
    User user = userCommandRepository.findById(userId)
                                     .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));


    return user;
  }

  @Override
  @Transactional
  public UserRewardInfo decreaseUserPoint(Long userId, Integer point) {
    User user = userCommandRepository.findById(userId)
                                     .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    user.decreasePoint(point);

    PointHistory pointHistory = PointHistory.builder()
                                            .user(user)
                                            .changeAmount(-point)
                                            .balance(user.getAmount())
                                            .type(PointType.USE)
                                            .description("경품 교환 차감")
                                            .build();

    pointCommandRepository.save(pointHistory);

    return new UserRewardInfo(user.getEmail(), user.getNickname(),
                              pointHistory.getId());
  }

  @Override
  @Transactional
  public void compensateUserPoint(Long userId, Long targetHistoryId) {
    User user = userCommandRepository.findById(userId)
                                     .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    PointHistory target = pointCommandRepository.findById(targetHistoryId)
                                                .orElseThrow(() -> new BusinessException(ErrorCode.POINT_HISTORY_NOT_FOUND));

    target.updateStatus(PointStatus.COMPENSATED);

    Integer compensationAmount = Math.abs(target.getChangeAmount());
    user.earnPoint(compensationAmount);

    PointHistory compensation = PointHistory.builder()
                                            .user(user)
                                            .changeAmount(compensationAmount)
                                            .balance(user.getAmount())
                                            .type(PointType.COMPENSATION)
                                            .description("보상 트랜잭션 복구 (원본 이력 ID: " + target.getId() + ")")
                                            .build();

    pointCommandRepository.save(compensation);
  }
}
