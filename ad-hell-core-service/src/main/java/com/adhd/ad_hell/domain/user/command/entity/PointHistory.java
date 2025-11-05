package com.adhd.ad_hell.domain.user.command.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private Integer changeAmount;
  private Integer balance;
  private String description;

  @Enumerated(EnumType.STRING)
  private PointType type;
  @Enumerated(EnumType.STRING)
  private PointStatus status;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Builder
  private PointHistory(User user, Integer changeAmount, PointType type, Integer balance, String description) {
    this.user = user;
    this.changeAmount = changeAmount;
    this.type = type;
    this.balance = balance;
    this.description = description;
    this.status = PointStatus.VALID;
    this.createdAt = LocalDateTime.now();
  }

  public void updateStatus(PointStatus status) {
    this.status = status;
  }
}
