package com.adhd.ad_hell.domain.user.command.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import com.adhd.ad_hell.common.dto.CustomUserDetails;
import com.adhd.ad_hell.domain.user.command.dto.request.UserPointRequest;
import com.adhd.ad_hell.domain.user.command.dto.response.UserPointResponse;
import com.adhd.ad_hell.domain.user.command.entity.PointHistory;
import com.adhd.ad_hell.domain.user.command.entity.PointType;
import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.PointCommandRepository;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class PointCommandServiceImplTest {

  @Mock private UserCommandRepository userCommandRepository;
  @Mock private PointCommandRepository pointCommandRepository;

  @InjectMocks private PointCommandServiceImpl pointCommandService;

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("earnPoints() - 로그인 유저 포인트 적립 성공")
  void earnPoints_success() {

    // given
    CustomUserDetails fakeUser = new CustomUserDetails(10L, "user@test.com", "pw", Role.USER);
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    User mockUser = User.builder()
                        .userId(10L)
                        .email("user@test.com")
                        .nickname("홍길동")
                        .amount(500)
                        .build();
    given(userCommandRepository.findByUserId(10L)).willReturn(Optional.of(mockUser));

    UserPointRequest request = new UserPointRequest(200);

    ArgumentCaptor<PointHistory> captor = ArgumentCaptor.forClass(PointHistory.class);

    // when
    UserPointResponse result = pointCommandService.earnPoints(request);

    // then
    assertThat(mockUser.getAmount()).isEqualTo(700); // 500 + 200
    assertThat(result.getAmount()).isEqualTo(700);
    assertThat(result.getMessage()).isEqualTo("포인트가 적립되었습니다.");

    verify(pointCommandRepository, times(1)).save(captor.capture());
    PointHistory savedHistory = captor.getValue();

    assertThat(savedHistory.getChangeAmount()).isEqualTo(200);
    assertThat(savedHistory.getBalance()).isEqualTo(700);
    assertThat(savedHistory.getDescription()).contains("포인트 적립");
    assertThat(savedHistory.getType()).isEqualTo(PointType.VIEW);
  }

  @Test
  @DisplayName("earnPoints() - 유저 미존재 시 예외 발생")
  void earnPoints_userNotFound() {

    // given
    CustomUserDetails fakeUser = new CustomUserDetails(99L, "ghost@test.com", "pw", Role.USER);
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    given(userCommandRepository.findByUserId(99L)).willReturn(Optional.empty());
    UserPointRequest request = new UserPointRequest(300);

    // then
    assertThatThrownBy(() -> pointCommandService.earnPoints(request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
  }
}
