package com.adhd.ad_hell.domain.report.query.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import com.adhd.ad_hell.common.dto.CustomUserDetails;
import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.report.command.domain.aggregate.ReportStatus;
import com.adhd.ad_hell.domain.report.query.dto.request.ReportSearchRequest;
import com.adhd.ad_hell.domain.report.query.dto.response.ReportDetailResponse;
import com.adhd.ad_hell.domain.report.query.dto.response.ReportListResponse;
import com.adhd.ad_hell.domain.report.query.dto.response.ReportResponse;
import com.adhd.ad_hell.domain.report.query.mapper.ReportMapper;
import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ReportQueryServiceTest {

  @Mock
  private ReportMapper reportMapper;

  @InjectMocks
  private ReportQueryService reportQueryService;

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  // ============================
  // getReportDetail()
  // ============================

  @Test
  @DisplayName("getReportDetail() - 정상조회 성공")
  void getReportDetail_success() {
    ReportDetailResponse mockDetail = ReportDetailResponse.builder()
        .categoryId(1L)
        .targetId(1L)
        .reporterId(1L)
        .status(ReportStatus.REQUEST)
        .reasonDetail("사유")
        .build();
    given(reportMapper.findReportById(1L)).willReturn(mockDetail);

    ReportDetailResponse result = reportQueryService.getReportDetail(1L);

    assertThat(result).isNotNull();
    assertThat(result.getReporterId()).isEqualTo(1L);
    verify(reportMapper, times(1)).findReportById(1L);
  }

  @Test
  @DisplayName("getReportDetail() - 존재하지 않을 경우 예외 발생")
  void getReportDetail_notFound() {
    given(reportMapper.findReportById(1L)).willReturn(null);

    assertThatThrownBy(() -> reportQueryService.getReportDetail(1L))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.REWARD_NOT_FOUND.getMessage());
  }

  // ============================
  // getReportList()
  // ============================

  @Test
  @DisplayName("getReportList() - 정상조회 및 페이지네이션 계산 확인")
  void getReportList_success() {
    ReportSearchRequest req = new ReportSearchRequest();
    req.setPage(1);
    req.setSize(10);

    List<ReportResponse> mockList = List.of(
        ReportResponse.builder()
                      .id(1L)
                      .categoryId(1L)
                      .reporterId(1L)
                      .build(),
        ReportResponse.builder()
                      .id(2L)
                      .reporterId(2L)
                      .categoryId(2L)
                      .build()
    );

    given(reportMapper.findReportList(req)).willReturn(mockList);
    given(reportMapper.countReports(req)).willReturn(2L);

    ReportListResponse result = reportQueryService.getReportList(req);

    assertThat(result.getReports()).hasSize(2);
    Pagination pagination = result.getPagination();
    assertThat(pagination.getTotalItems()).isEqualTo(2L);
    assertThat(pagination.getTotalPages()).isEqualTo(1);
  }

  // ============================
  // getMyReports()
  // ============================

  @Test
  @DisplayName("getMyReports() - 로그인 유저 정보 기반 조회 성공")
  void getMyReports_success() {
    // 🔹 SecurityContext 세팅
    CustomUserDetails fakeUser = new CustomUserDetails(10L, "user@test.com", "pw", Role.USER);
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    ReportSearchRequest req = new ReportSearchRequest();
    req.setPage(1);
    req.setSize(5);

    List<ReportResponse> mockList = List.of(
        ReportResponse.builder()
                      .id(1L)
                      .categoryId(1L)
                      .reporterId(1L)
                      .build(),
        ReportResponse.builder()
                      .id(2L)
                      .reporterId(2L)
                      .categoryId(2L)
                      .build()
    );
    given(reportMapper.findReportsByUserId(req)).willReturn(mockList);
    given(reportMapper.countMyReports(req)).willReturn(2L);

    ReportListResponse result = reportQueryService.getMyReports(req);

    assertThat(result.getReports()).hasSize(2);
    assertThat(result.getPagination().getTotalItems()).isEqualTo(2L);
    assertThat(req.getUserId()).isEqualTo(10L);
  }

  // ============================
  // getMyReportDetail()
  // ============================

  @Test
  @DisplayName("getMyReportDetail() - 본인 신고 조회 성공")
  void getMyReportDetail_success() {
    CustomUserDetails fakeUser = new CustomUserDetails(10L, "user@test.com", "pw", Role.USER);
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    ReportDetailResponse mockDetail =
        ReportDetailResponse.builder()
                            .id(1L)
                            .categoryId(1L)
                            .targetId(99L)
                            .reasonDetail("신고 사유")
                            .reporterId(10L)
                            .build();
    given(reportMapper.findReportById(1L)).willReturn(mockDetail);

    ReportDetailResponse result = reportQueryService.getMyReportDetail(1L);

    assertThat(result).isNotNull();
    assertThat(result.getReporterId()).isEqualTo(10L);
  }

  @Test
  @DisplayName("getMyReportDetail() - 본인 신고가 아닐 경우 ACCESS_DENIED 예외 발생")
  void getMyReportDetail_notOwner() {
    CustomUserDetails fakeUser = new CustomUserDetails(10L, "user@test.com", "pw", Role.USER);
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    ReportDetailResponse mockDetail =
        ReportDetailResponse.builder()
                            .id(1L)
                            .categoryId(1L)
                            .targetId(99L)
                            .reasonDetail("신고 사유")
                            .reporterId(20L)
                            .build();
    given(reportMapper.findReportById(1L)).willReturn(mockDetail);

    assertThatThrownBy(() -> reportQueryService.getMyReportDetail(1L))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.ACCESS_DENIED.getMessage());
  }

  @Test
  @DisplayName("getMyReportDetail() - 존재하지 않을 경우 REPORT_NOT_FOUND 예외 발생")
  void getMyReportDetail_notFound() {
    CustomUserDetails fakeUser = new CustomUserDetails(10L, "user@test.com", "pw", Role.USER);
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    given(reportMapper.findReportById(1L)).willReturn(null);

    assertThatThrownBy(() -> reportQueryService.getMyReportDetail(1L))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.REPORT_NOT_FOUND.getMessage());
  }
}
