package com.adhd.ad_hell.domain.reward.command.application.service;

import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.category.query.service.provider.CategoryProvider;
import com.adhd.ad_hell.domain.reward.command.application.dto.request.CreateRewardRequest;
import com.adhd.ad_hell.domain.reward.command.application.dto.request.CreateRewardStockRequest;
import com.adhd.ad_hell.domain.reward.command.application.dto.request.UpdateRewardRequest;
import com.adhd.ad_hell.domain.reward.command.domain.aggregate.Reward;
import com.adhd.ad_hell.domain.reward.command.domain.aggregate.RewardStatus;
import com.adhd.ad_hell.domain.reward.command.domain.aggregate.RewardStock;
import com.adhd.ad_hell.domain.reward.command.domain.aggregate.RewardStockStatus;
import com.adhd.ad_hell.domain.reward.command.domain.repository.RewardRepository;
import com.adhd.ad_hell.domain.reward.command.domain.repository.RewardStockRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.query.dto.response.UserRewardInfo;
import com.adhd.ad_hell.domain.user.query.service.provider.UserProvider;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import com.adhd.ad_hell.mail.MailService;
import com.adhd.ad_hell.mail.MailType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class RewardCommandServiceImpl implements RewardCommandService, RewardStockCommandService {

  private final RewardRepository rewardRepository;
  private final RewardStockRepository rewardStockRepository;
  private final CategoryProvider categoryProvider;
  private final MailService mailService;
  private final SecurityUtil securityUtil;
  private final UserProvider userProvider;

  @Transactional
  public void createReward(CreateRewardRequest req) {
    Reward reward = Reward.builder()
        .category(categoryProvider.getCategoryEntityById(req.getCategoryId()))
        .name(req.getName())
        .description(req.getDescription())
        .pointCost(req.getPointCost())
        .stock(req.getStock())
        .status(RewardStatus.ACTIVATE)
        .build();

    rewardRepository.save(reward);
  }

  @Transactional
  public void updateReward(Long rewardId, UpdateRewardRequest req) {
    Reward reward = rewardRepository.findById(rewardId)
                                    .orElseThrow(()-> new BusinessException(ErrorCode.REWARD_NOT_FOUND));

    reward.updateInfo(req.getName(), req.getDescription(), req.getPointCost(), req.getStock());
  }

  @Transactional
  public void toggleStatusReward(Long rewardId) {
    Reward reward = rewardRepository.findById(rewardId)
                                    .orElseThrow(()-> new BusinessException(ErrorCode.REWARD_NOT_FOUND));

    reward.toggleStatus();
  }

  @Transactional
  public void deleteReward(Long rewardId) {
    rewardRepository.deleteById(rewardId);
  }

  @Transactional
  public void createRewardStock(Long rewardId, CreateRewardStockRequest req) {
    Reward reward = rewardRepository.findById(rewardId)
                                    .orElseThrow(()-> new BusinessException(ErrorCode.REWARD_NOT_FOUND));

    reward.incrementStock();

    RewardStock rewardStock = RewardStock.builder()
        .reward(reward)
        .pinNumber(req.getPinNumber())
        .expiredAt(req.getExpiredAt())
        .build();

    rewardStockRepository.save(rewardStock);
  }

  @Transactional
  public void sendReward(Long rewardId) {
    Reward reward = rewardRepository.findById(rewardId)
                                    .orElseThrow(()-> new BusinessException(ErrorCode.REWARD_NOT_FOUND));

    Long userId = securityUtil.getLoginUserInfo().getUserId();

    UserRewardInfo tempInfo;
    try {
      tempInfo = userProvider.decreaseUserPoint(userId, reward.getPointCost());
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.POINT_DECREASE_FAILED);
    }
    final UserRewardInfo userRewardInfo = tempInfo;

    reward.decrementStock();

    RewardStock stock = rewardStockRepository
        .findFirstByRewardAndStatusOrderByCreatedAtAsc(reward, RewardStockStatus.ACTIVATE)
        .orElseThrow(() -> new BusinessException(ErrorCode.REWARD_STOCK_INVALID_STATUS));

    if (stock.isExpired()) {
      stock.expire();
      throw new BusinessException(ErrorCode.REWARD_STOCK_INVALID_STATUS);
    }

    stock.markAsUsed();


    TransactionSynchronizationManager.registerSynchronization(
        // registerSynchronization 메소드를 통해 TransactionSynchronization 구현체
        // 를 등록하면 트랜잭션의 커밋 또는 롤백 직후 로직을 정의할 수 있음
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (userRewardInfo == null) return;
            if(status != STATUS_COMMITTED) {
              userProvider.compensateUserPoint(userId, userRewardInfo.pointHistoryId());
            }
          }
        }
    );

    try {
      String code = stock.getPinNumber();
      mailService.sendMail(userRewardInfo.email(), userRewardInfo.name(), MailType.REWARD, code);
    } catch (Exception e) {
      throw new RuntimeException("메일 전송 실패");
    }
  }
}
