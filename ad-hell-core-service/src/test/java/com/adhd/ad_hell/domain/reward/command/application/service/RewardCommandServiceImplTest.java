package com.adhd.ad_hell.domain.reward.command.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import com.adhd.ad_hell.common.dto.CustomUserDetails;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.category.query.service.provider.CategoryProvider;
import com.adhd.ad_hell.domain.reward.command.application.dto.request.CreateRewardRequest;
import com.adhd.ad_hell.domain.reward.command.application.dto.request.CreateRewardStockRequest;
import com.adhd.ad_hell.domain.reward.command.application.dto.request.UpdateRewardRequest;
import com.adhd.ad_hell.domain.reward.command.domain.aggregate.*;
import com.adhd.ad_hell.domain.reward.command.domain.repository.RewardRepository;
import com.adhd.ad_hell.domain.reward.command.domain.repository.RewardStockRepository;
import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.query.dto.response.UserRewardInfo;
import com.adhd.ad_hell.domain.user.query.service.provider.UserProvider;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import com.adhd.ad_hell.mail.MailService;
import com.adhd.ad_hell.mail.MailType;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(MockitoExtension.class)
class RewardCommandServiceImplTest {

  @Mock private RewardRepository rewardRepository;
  @Mock private RewardStockRepository rewardStockRepository;
  @Mock private CategoryProvider categoryProvider;
  @Mock private MailService mailService;
  @Mock private UserProvider userProvider;
  @InjectMocks private RewardCommandServiceImpl rewardCommandService;

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  // ======================================
  // createReward()
  // ======================================
  @Test
  @DisplayName("createReward() - 정상 생성 성공")
  void createReward_success() {
    Category mockCategory = Category.builder().name("음료").description("테스트").build();
    given(categoryProvider.getCategoryEntityById(1L)).willReturn(mockCategory);

    CreateRewardRequest req = new CreateRewardRequest(1L, "콜라", "시원한 콜라", 100, 10);

    rewardCommandService.createReward(req);

    verify(rewardRepository, times(1)).save(any(Reward.class));
  }

  // ======================================
  // updateReward()
  // ======================================
  @Test
  @DisplayName("updateReward() - 존재하지 않으면 예외")
  void updateReward_notFound() {
    given(rewardRepository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() ->
                           rewardCommandService.updateReward(99L, new UpdateRewardRequest("n", "d", 1, 1))
    ).isInstanceOf(BusinessException.class)
     .hasMessageContaining(ErrorCode.REWARD_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("updateReward() - 정상 업데이트 성공")
  void updateReward_success() {
    Reward reward = Reward.builder()
                          .name("콜라")
                          .description("시원한 콜라")
                          .pointCost(100)
                          .stock(5)
                          .status(RewardStatus.ACTIVATE)
                          .build();

    given(rewardRepository.findById(1L)).willReturn(Optional.of(reward));

    UpdateRewardRequest req = new UpdateRewardRequest("제로콜라", "무당콜라", 150, 3);

    rewardCommandService.updateReward(1L, req);

    assertThat(reward.getName()).isEqualTo("제로콜라");
    assertThat(reward.getDescription()).isEqualTo("무당콜라");
    assertThat(reward.getPointCost()).isEqualTo(150);
    assertThat(reward.getStock()).isEqualTo(3);
  }

  // ======================================
  // toggleStatusReward()
  // ======================================
  @Test
  @DisplayName("toggleStatusReward() - 상태 토글 성공")
  void toggleStatusReward_success() {
    Reward reward = Reward.builder().status(RewardStatus.ACTIVATE).build();
    given(rewardRepository.findById(1L)).willReturn(Optional.of(reward));

    rewardCommandService.toggleStatusReward(1L);

    assertThat(reward.getStatus()).isEqualTo(RewardStatus.DEACTIVATE);
  }

  // ======================================
  // deleteReward()
  // ======================================
  @Test
  @DisplayName("deleteReward() - 정상 삭제")
  void deleteReward_success() {
    rewardCommandService.deleteReward(1L);
    verify(rewardRepository).deleteById(1L);
  }

  // ======================================
  // createRewardStock()
  // ======================================
  @Test
  @DisplayName("createRewardStock() - 정상 재고 생성")
  void createRewardStock_success() {
    Reward reward = Reward.builder().stock(1).status(RewardStatus.ACTIVATE).build();
    given(rewardRepository.findById(1L)).willReturn(Optional.of(reward));

    CreateRewardStockRequest req = new CreateRewardStockRequest("PIN123", LocalDateTime.now().plusDays(7));

    rewardCommandService.createRewardStock(1L, req);

    assertThat(reward.getStock()).isEqualTo(2);
    verify(rewardStockRepository).save(any(RewardStock.class));
  }

  // ======================================
  // sendReward()
  // ======================================
  @Test
  @DisplayName("sendReward() - 정상 발송 성공")
  void sendReward_success() {
    TransactionSynchronizationManager.initSynchronization();

    CustomUserDetails fakeUser = new CustomUserDetails(10L, "user@test.com", "pw", Role.USER);
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    Reward reward = Reward.builder().pointCost(100).stock(5).status(RewardStatus.ACTIVATE).build();
    given(rewardRepository.findById(1L)).willReturn(Optional.of(reward));

    RewardStock stock = RewardStock.builder()
                                   .reward(reward)
                                   .pinNumber("PINCODE123")
                                   .expiredAt(LocalDateTime.now().plusDays(1))
                                   .build();
    given(rewardStockRepository.findFirstByRewardAndStatusOrderByCreatedAtAsc(reward, RewardStockStatus.ACTIVATE))
        .willReturn(Optional.of(stock));

    UserRewardInfo info = new UserRewardInfo("user@test.com", "테스터", 99L);
    given(userProvider.decreaseUserPoint(10L, reward.getPointCost())).willReturn(info);

    willDoNothing().given(mailService)
                   .sendMail(info.email(), info.name(), MailType.REWARD, stock.getPinNumber());

    rewardCommandService.sendReward(1L);

    verify(userProvider, never()).compensateUserPoint(any(), any());
    verify(mailService).sendMail(info.email(), info.name(), MailType.REWARD, stock.getPinNumber());
  }

  @Test
  @DisplayName("sendReward() - 포인트 차감 실패 시 예외 발생")
  void sendReward_pointFail() {
    CustomUserDetails fakeUser = new CustomUserDetails(10L, "user@test.com", "pw", Role.USER);
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    Reward reward = Reward.builder().pointCost(100).build();
    given(rewardRepository.findById(1L)).willReturn(Optional.of(reward));
    willThrow(RuntimeException.class).given(userProvider).decreaseUserPoint(any(), any());

    assertThatThrownBy(() -> rewardCommandService.sendReward(1L))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.POINT_DECREASE_FAILED.getMessage());
  }

  @Test
  @DisplayName("sendReward() - 재고 만료 시 예외 발생")
  void sendReward_expiredStock() {
    CustomUserDetails fakeUser = new CustomUserDetails(10L, "user@test.com", "pw", Role.USER);
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(fakeUser, null, fakeUser.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);

    Reward reward = Reward.builder().stock(0).pointCost(100).build();
    given(rewardRepository.findById(1L)).willReturn(Optional.of(reward));

    RewardStock expiredStock = RewardStock.builder()
                                          .reward(reward)
                                          .pinNumber("PIN")
                                          .expiredAt(LocalDateTime.now().minusDays(1))
                                          .build();

    given(userProvider.decreaseUserPoint(any(), any()))
        .willReturn(new UserRewardInfo("user@test.com", "테스터", 11L));

    given(rewardStockRepository.findFirstByRewardAndStatusOrderByCreatedAtAsc(reward, RewardStockStatus.ACTIVATE))
        .willReturn(Optional.of(expiredStock));

    assertThatThrownBy(() -> rewardCommandService.sendReward(1L))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.REWARD_STOCK_INVALID_STATUS.getMessage());
  }
}
