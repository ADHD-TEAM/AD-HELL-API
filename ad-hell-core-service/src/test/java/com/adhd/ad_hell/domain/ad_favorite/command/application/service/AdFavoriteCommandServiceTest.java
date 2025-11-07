package com.adhd.ad_hell.domain.ad_favorite.command.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import com.adhd.ad_hell.domain.ad_favorite.command.application.dto.request.AdFavoriteCreateRequest;
import com.adhd.ad_hell.domain.ad_favorite.command.application.dto.response.AdFavoriteCommandResponse;
import com.adhd.ad_hell.domain.ad_favorite.command.domain.aggregate.AdFavorite;
import com.adhd.ad_hell.domain.ad_favorite.command.domain.repository.AdFavoriteRepository;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdFavoriteCommandServiceTest {

    @Mock private AdFavoriteRepository adFavoriteRepository;
    @Mock private AdRepository adRepository;
    @Mock private UserCommandRepository userRepository;

    @InjectMocks private AdFavoriteCommandService service;

    @Nested
    @DisplayName("AdFavoriteCreate()")
    class CreateTests {

        @Test
        @DisplayName("정상 등록 성공")
        void create_success() {
            // given
            AdFavoriteCreateRequest req = AdFavoriteCreateRequest.builder()
                    .userId(10L).adId(100L).build();

            Ad ad = mock(Ad.class);
            User user = mock(User.class);
            AdFavorite saved = mock(AdFavorite.class);

            given(adRepository.findById(100L)).willReturn(Optional.of(ad));
            given(userRepository.findById(10L)).willReturn(Optional.of(user));
            given(user.getUserId()).willReturn(10L);
            given(ad.getAdId()).willReturn(100L);
            given(adFavoriteRepository.exists(10L, 100L)).willReturn(false);

            given(saved.getId()).willReturn(999L);
            LocalDateTime now = LocalDateTime.now();
            given(saved.getCreatedAt()).willReturn(now);
            // factory 메서드는 실제 정적 호출이므로, save만 모의로 대체
            given(adFavoriteRepository.save(any(AdFavorite.class))).willReturn(saved);

            // when
            AdFavoriteCommandResponse resp = service.AdFavoriteCreate(req);

            // then
            assertThat(resp.getFavId()).isEqualTo(999L);
            assertThat(resp.getUserId()).isEqualTo(10L);
            assertThat(resp.getAdId()).isEqualTo(100L);
            assertThat(resp.getCreatedAt()).isEqualTo(now);

            verify(adRepository).findById(100L);
            verify(userRepository).findById(10L);
            verify(adFavoriteRepository).exists(10L, 100L);
            verify(adFavoriteRepository).save(any(AdFavorite.class));
        }

        @Test
        @DisplayName("광고 없음 → AD_NOT_FOUND")
        void create_adNotFound() {
            AdFavoriteCreateRequest req = AdFavoriteCreateRequest.builder()
                    .userId(10L).adId(100L).build();

            given(adRepository.findById(100L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.AdFavoriteCreate(req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.AD_NOT_FOUND.getMessage());

            verify(adRepository).findById(100L);
            verifyNoInteractions(userRepository, adFavoriteRepository);
        }

        @Test
        @DisplayName("사용자 없음 → USER_NOT_FOUND")
        void create_userNotFound() {
            AdFavoriteCreateRequest req = AdFavoriteCreateRequest.builder()
                    .userId(10L).adId(100L).build();

            Ad ad = mock(Ad.class);
            given(adRepository.findById(100L)).willReturn(Optional.of(ad));
            given(userRepository.findById(10L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.AdFavoriteCreate(req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());

            verify(adRepository).findById(100L);
            verify(userRepository).findById(10L);
            verifyNoInteractions(adFavoriteRepository);
        }

        @Test
        @DisplayName("이미 즐겨찾기 존재 → AD_FAVORITE_ALREADY_EXISTS")
        void create_duplicate() {
            AdFavoriteCreateRequest req = AdFavoriteCreateRequest.builder()
                    .userId(10L).adId(100L).build();

            Ad ad = mock(Ad.class);
            User user = mock(User.class);

            given(adRepository.findById(100L)).willReturn(Optional.of(ad));
            given(userRepository.findById(10L)).willReturn(Optional.of(user));
            given(user.getUserId()).willReturn(10L);
            given(ad.getAdId()).willReturn(100L);
            given(adFavoriteRepository.exists(10L, 100L)).willReturn(true);

            assertThatThrownBy(() -> service.AdFavoriteCreate(req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.AD_FAVORITE_ALREADY_EXISTS.getMessage());

            verify(adFavoriteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("AdFavoriteDelete()")
    class DeleteTests {

        @Test
        @DisplayName("정상 삭제")
        void delete_success() {
            given(adFavoriteRepository.exists(10L, 100L)).willReturn(true);

            service.AdFavoriteDelete(10L, 100L);

            verify(adFavoriteRepository).delete(10L, 100L);
        }

        @Test
        @DisplayName("대상 없음 → AD_FAVORITE_NOT_FOUND")
        void delete_notFound() {
            given(adFavoriteRepository.exists(10L, 100L)).willReturn(false);

            assertThatThrownBy(() -> service.AdFavoriteDelete(10L, 100L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.AD_FAVORITE_NOT_FOUND.getMessage());

            verify(adFavoriteRepository, never()).delete(anyLong(), anyLong());
        }
    }
}
