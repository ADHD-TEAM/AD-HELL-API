package com.adhd.ad_hell.domain.reward.command.application.service;

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
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import com.adhd.ad_hell.mail.MailService;
import com.adhd.ad_hell.mail.MailType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RewardCommandServiceImpl implements RewardCommandService, RewardStockCommandService {

  private final RewardRepository rewardRepository;
  private final RewardStockRepository rewardStockRepository;
  private final CategoryProvider categoryProvider;
  private final MailService mailService;

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


    reward.decrementStock();

    RewardStock stock = rewardStockRepository
        .findFirstByRewardAndStatusOrderByCreatedAtAsc(reward, RewardStockStatus.ACTIVATE)
        .orElseThrow(() -> new BusinessException(ErrorCode.REWARD_STOCK_INVALID_STATUS));

    if (stock.isExpired()) {
      stock.expire();
      throw new BusinessException(ErrorCode.REWARD_STOCK_INVALID_STATUS);
    }

    stock.markAsUsed();

    try {
      String code = stock.getPinNumber();
      mailService.sendMail("zkvn1103@naver.com", "이민욱", MailType.REWARD, code);
    } catch (Exception e) {
      throw new RuntimeException("메일 전송 실패");
    }
  }
}
