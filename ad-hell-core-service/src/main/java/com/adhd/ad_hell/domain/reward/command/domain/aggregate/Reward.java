package com.adhd.ad_hell.domain.reward.command.domain.aggregate;

import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE reward SET status = 'DELETE' where id = ?")
public class Reward extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "category_id",
      foreignKey = @ForeignKey(name = "fk_reward_category")
  )
  private Category category;

  private String name;
  private String description;
  private Integer pointCost;
  private Integer stock;

  @Enumerated(EnumType.STRING)
  private RewardStatus status;

  @Builder
  public Reward(Long id, Category category, String name, String description, Integer pointCost, Integer stock, RewardStatus status) {
    this.id = id;
    this.category = category;
    this.name = name;
    this.description = description;
    this.pointCost = pointCost;
    this.stock = stock;
    this.status = status;
  }

  public void updateInfo(String name, String description, Integer pointCost, Integer stock) {
    if (StringUtils.hasText(name)) {
      this.name = name;
    }
    if (StringUtils.hasText(description)) {
      this.description = description;
    }
    if (pointCost != null) {
      this.pointCost = pointCost;
    }
    if (stock != null) {
      this.stock = stock;
    }
  }

  public void toggleStatus() {
    this.status = this.status.toggle();
  }

  public void incrementStock() {
    this.stock += 1;
  }

  public void decrementStock() {
    this.stock -= 1;
  }
}
