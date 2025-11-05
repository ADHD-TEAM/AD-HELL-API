package com.adhd.ad_hell.domain.report.command.application.service;

import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.category.query.service.provider.CategoryProvider;
import com.adhd.ad_hell.domain.report.command.application.dto.request.CreateReportRequest;
import com.adhd.ad_hell.domain.report.command.domain.aggregate.Report;
import com.adhd.ad_hell.domain.report.command.domain.repository.ReportRepository;
import com.adhd.ad_hell.domain.user.query.service.provider.UserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportCommandService {

  private final ReportRepository reportRepository;
  private final CategoryProvider categoryProvider;
  private final SecurityUtil securityUtil;
  private final UserProvider userProvider;

  @Transactional
  public void createReport(CreateReportRequest req) {
    Category category = categoryProvider.getCategoryEntityById(req.getCategoryId());

    Long userId = securityUtil.getLoginUserInfo().getUserId();

    Report report = Report.builder()
        .category(category)
        .targetId(req.getTargetId())
        .reasonDetail(req.getReasonDetail())
        .reporter(userProvider.getUserById(userId))
        .build();

    reportRepository.save(report);
  }
}
