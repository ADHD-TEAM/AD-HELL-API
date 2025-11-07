package com.adhd.ad_hell.domain.reward.command.domain.aggregate;

import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE reward_stock SET status = 'DELETE' where id = ?")
public class RewardStock extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "reward_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_reward_stock_reward")
  )
  private Reward reward;

  @Column(name = "pin_number", nullable = false, unique = true, length = 50)
  private String pinNumber;

  @Enumerated(EnumType.STRING)
  private RewardStockStatus status;

  private LocalDateTime expiredAt;

  @Builder
  private RewardStock(Reward reward, String pinNumber, LocalDateTime expiredAt) {
    this.reward = reward;
    this.pinNumber = pinNumber;
    this.expiredAt = expiredAt;
    this.status = RewardStockStatus.ACTIVATE;
  }

  public void markAsUsed() {
    if (this.status != RewardStockStatus.ACTIVATE) {
      throw new BusinessException(ErrorCode.REWARD_STOCK_INVALID_STATUS);
    }
    this.status = RewardStockStatus.USED;
  }

  public void expire() {
    if (this.status == RewardStockStatus.ACTIVATE) {
      this.status = RewardStockStatus.EXPIRED;
    }
  }

  public boolean isExpired() {
    return expiredAt != null && expiredAt.isBefore(LocalDateTime.now());
  }
}
