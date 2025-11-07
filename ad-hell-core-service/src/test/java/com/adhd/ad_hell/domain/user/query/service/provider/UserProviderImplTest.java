package com.adhd.ad_hell.domain.user.query.service.provider;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import com.adhd.ad_hell.domain.user.command.entity.*;
import com.adhd.ad_hell.domain.user.command.repository.PointCommandRepository;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.domain.user.query.dto.response.UserRewardInfo;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProviderImplTest {

  @Mock private UserCommandRepository userCommandRepository;
  @Mock private PointCommandRepository pointCommandRepository;

  @InjectMocks private UserProviderImpl userProvider;

  // ============================
  // getUserById()
  // ============================
  @Test
  @DisplayName("getUserById() - 유저 조회 성공")
  void getUserById_success() {
    User user = User.builder().userId(1L).email("user@test.com").nickname("홍길동").amount(500).build();
    given(userCommandRepository.findById(1L)).willReturn(Optional.of(user));

    User result = userProvider.getUserById(1L);

    assertThat(result.getEmail()).isEqualTo("user@test.com");
    verify(userCommandRepository).findById(1L);
  }

  @Test
  @DisplayName("getUserById() - 존재하지 않을 경우 예외 발생")
  void getUserById_notFound() {
    given(userCommandRepository.findById(1L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userProvider.getUserById(1L))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
  }

  // ============================
  // decreaseUserPoint()
  // ============================
  @Test
  @DisplayName("decreaseUserPoint() - 포인트 차감 및 이력 생성 성공")
  void decreaseUserPoint_success() {
    // given
    User user = User.builder()
                    .userId(1L)
                    .email("user@test.com")
                    .nickname("홍길동")
                    .amount(1000)
                    .build();
    given(userCommandRepository.findById(1L)).willReturn(Optional.of(user));

    ArgumentCaptor<PointHistory> captor = ArgumentCaptor.forClass(PointHistory.class);

    // when
    UserRewardInfo result = userProvider.decreaseUserPoint(1L, 300);

    // then
    verify(pointCommandRepository).save(captor.capture());
    PointHistory saved = captor.getValue();

    assertThat(saved.getChangeAmount()).isEqualTo(-300);
    assertThat(saved.getBalance()).isEqualTo(user.getAmount());
    assertThat(saved.getType()).isEqualTo(PointType.USE);
    assertThat(user.getAmount()).isEqualTo(700); // 포인트 감소
    assertThat(result.email()).isEqualTo("user@test.com");
    assertThat(result.name()).isEqualTo("홍길동");
  }

  @Test
  @DisplayName("decreaseUserPoint() - 유저 존재하지 않으면 예외 발생")
  void decreaseUserPoint_userNotFound() {
    given(userCommandRepository.findById(1L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userProvider.decreaseUserPoint(1L, 100))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
  }

  // ============================
  // compensateUserPoint()
  // ============================
  @Test
  @DisplayName("compensateUserPoint() - 포인트 복구 및 보상 이력 저장 성공")
  void compensateUserPoint_success() {
    // given
    User user = User.builder()
                    .userId(1L)
                    .email("user@test.com")
                    .nickname("홍길동")
                    .amount(500)
                    .build();

    PointHistory target = PointHistory.builder()
                                      .user(user)
                                      .changeAmount(-200)
                                      .balance(500)
                                      .type(PointType.USE)
                                      .description("테스트 차감")
                                      .build();

    given(userCommandRepository.findById(1L)).willReturn(Optional.of(user));
    given(pointCommandRepository.findById(10L)).willReturn(Optional.of(target));

    ArgumentCaptor<PointHistory> captor = ArgumentCaptor.forClass(PointHistory.class);

    // when
    userProvider.compensateUserPoint(1L, 10L);

    // then
    assertThat(target.getStatus()).isEqualTo(PointStatus.COMPENSATED);
    assertThat(user.getAmount()).isEqualTo(700);
    verify(pointCommandRepository, times(1)).save(captor.capture());
    PointHistory savedCompensation = captor.getValue();
    assertThat(savedCompensation.getType()).isEqualTo(PointType.COMPENSATION);
    assertThat(savedCompensation.getDescription()).contains("보상 트랜잭션 복구");
  }

  @Test
  @DisplayName("compensateUserPoint() - 유저 없음 예외")
  void compensateUserPoint_userNotFound() {
    given(userCommandRepository.findById(1L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userProvider.compensateUserPoint(1L, 10L))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("compensateUserPoint() - 포인트 이력 없음 예외")
  void compensateUserPoint_pointHistoryNotFound() {
    User user = User.builder().userId(1L).amount(100).build();
    given(userCommandRepository.findById(1L)).willReturn(Optional.of(user));
    given(pointCommandRepository.findById(10L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userProvider.compensateUserPoint(1L, 10L))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.POINT_HISTORY_NOT_FOUND.getMessage());
  }
}
