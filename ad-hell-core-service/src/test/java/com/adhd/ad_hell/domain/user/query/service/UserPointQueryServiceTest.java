package com.adhd.ad_hell.domain.user.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.adhd.ad_hell.common.dto.CustomUserDetails;
import com.adhd.ad_hell.domain.user.command.entity.PointStatus;
import com.adhd.ad_hell.domain.user.command.entity.PointType;
import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.query.dto.response.UserPointHistoryResponse;
import com.adhd.ad_hell.domain.user.query.mapper.PointHistoryMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class UserPointQueryServiceTest {

  @Mock
  private PointHistoryMapper pointHistoryMapper;

  @InjectMocks
  private UserPointQueryService userPointQueryService;

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("getMyPointHistory() - 정상 조회 성공")
  void getMyPointHistory_success() {
    CustomUserDetails fakeUser = new CustomUserDetails(10L, "user@test.com", "pw", Role.USER);
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    List<UserPointHistoryResponse> mockList = List.of(
        UserPointHistoryResponse.builder()
                                .id(1L)
                                .changeAmount(+100)
                                .balance(1100)
                                .description("적립")
                                .type(PointType.EARN)
                                .createdAt(LocalDateTime.now())
                                .build(),
        UserPointHistoryResponse.builder()
                                .id(2L)
                                .changeAmount(-50)
                                .balance(1050)
                                .description("차감")
                                .type(PointType.USE)
                                .createdAt(LocalDateTime.now())
                                .build()
    );

    given(pointHistoryMapper.findMyPointHistory(10L, PointStatus.VALID))
        .willReturn(mockList);

    List<UserPointHistoryResponse> result = userPointQueryService.getMyPointHistory();

    verify(pointHistoryMapper).findMyPointHistory(10L, PointStatus.VALID);
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getDescription()).isEqualTo("적립");
    assertThat(result.get(1).getType()).isEqualTo(PointType.USE);
  }
}
