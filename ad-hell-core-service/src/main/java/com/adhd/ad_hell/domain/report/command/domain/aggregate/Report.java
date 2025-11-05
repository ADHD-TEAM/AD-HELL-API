package com.adhd.ad_hell.domain.report.command.domain.aggregate;

import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.user.command.entity.User;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "category_id",
      foreignKey = @ForeignKey(name = "fk_report_category")
  )
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "reporter_id",
      foreignKey = @ForeignKey(name = "fk_report_user")
  )
  private User reporter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "admin_id",
      foreignKey = @ForeignKey(name = "fk_report_admin")
  )
  private User admin;

  private Long targetId;
  private String reasonDetail;
  @Enumerated(EnumType.STRING)
  private ReportStatus status;

  @Builder Report(Category category, User reporter, Long targetId, String reasonDetail) {
    this.category = category;
    this.reporter = reporter;
    this.targetId = targetId;
    this.reasonDetail = reasonDetail;
    this.status = ReportStatus.REQUEST;
  }

}
