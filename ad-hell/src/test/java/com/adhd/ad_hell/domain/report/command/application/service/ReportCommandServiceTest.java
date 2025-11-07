package com.adhd.ad_hell.domain.report.command.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.adhd.ad_hell.common.dto.CustomUserDetails;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.CategoryStatus;
import com.adhd.ad_hell.domain.category.query.service.provider.CategoryProvider;
import com.adhd.ad_hell.domain.report.command.application.dto.request.CreateReportRequest;
import com.adhd.ad_hell.domain.report.command.domain.aggregate.Report;
import com.adhd.ad_hell.domain.report.command.domain.repository.ReportRepository;
import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.query.service.provider.UserProvider;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ReportCommandServiceTest {

  @Mock
  private ReportRepository reportRepository;

  @Mock
  private CategoryProvider categoryProvider;

  @Mock
  private UserProvider userProvider;

  @InjectMocks
  private ReportCommandService reportCommandService;

  @Captor
  private ArgumentCaptor<Report> reportCaptor;

  @Test
  @DisplayName("신고 생성 성공 - 카테고리, 리포터, 타겟 정상 주입")
  void createReport() {
    // given
    CustomUserDetails fakeUserDetails = new CustomUserDetails(10L, "test@user.com", "password", Role.USER);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(fakeUserDetails, null, fakeUserDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    CreateReportRequest req = new CreateReportRequest(1L, 99L, "부적절한 게시물");

    Category mockCategory = Category.builder()
        .name("테스트 카테고리명")
        .description("테스트 카테고리 내용")
        .status(CategoryStatus.ACTIVATE)
        .parent(null).build();
    given(categoryProvider.getCategoryEntityById(1L)).willReturn(mockCategory);

    User mockUser = User.builder()
        .userId(10L)
        .build();
    given(userProvider.getUserById(10L)).willReturn(mockUser);

    // when
    reportCommandService.createReport(req);

    // then
    verify(reportRepository, times(1)).save(reportCaptor.capture());
    Report savedReport = reportCaptor.getValue();
    assertThat(savedReport.getCategory()).isEqualTo(mockCategory);
    assertThat(savedReport.getReporter()).isEqualTo(mockUser);
    assertThat(savedReport.getTargetId()).isEqualTo(99L);
    assertThat(savedReport.getReasonDetail()).isEqualTo("부적절한 게시물");

  }
}