package com.adhd.ad_hell.domain.ad_favorite.query.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.request.AdFavoriteSearchRequest;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.response.AdFavoriteDTO;
import com.adhd.ad_hell.domain.ad_favorite.query.dto.response.AdFavoriteListResponse;
import com.adhd.ad_hell.domain.ad_favorite.query.mapper.AdFavoriteMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdFavoriteQueryServiceTest {

    @Mock private AdFavoriteMapper mapper;
    @InjectMocks private AdFavoriteQueryService service;

    @Nested
    @DisplayName("getMyFavorite()")
    class GetMyFavoriteTests {

        @Test
        @DisplayName("목록/카운트 정상 반환 + 페이징 계산 검증")
        void getMyFavorite_success() {
            // given
            AdFavoriteSearchRequest req = new AdFavoriteSearchRequest(); // page/size가 null이면 서비스가 기본값 보정

            AdFavoriteDTO row = new AdFavoriteDTO();
            when(mapper.findMyFavorites(req)).thenReturn(List.of(row, row));
            when(mapper.countMyFavorites(req)).thenReturn(2L);

            // when
            AdFavoriteListResponse res = service.getMyFavorite(req);

            // then
            assertThat(res).isNotNull();
            assertThat(res.getAdFavorites()).hasSize(2);

            Pagination p = res.getPagination();
            assertThat(p).isNotNull();
            assertThat(p.getTotalItems()).isEqualTo(2L);
            assertThat(p.getCurrentPage()).isEqualTo(1);   // 기본 page 보정 가정
            assertThat(p.getTotalPages()).isGreaterThanOrEqualTo(1);

            verify(mapper).findMyFavorites(req);
            verify(mapper).countMyFavorites(req);
        }
    }

    @Nested
    @DisplayName("getFavoriteDetail()")
    class GetFavoriteDetailTests {

        @Test
        @DisplayName("상세 DTO 반환")
        void getFavoriteDetail_success() {
            Long favId = 99L;
            AdFavoriteDTO dto = new AdFavoriteDTO();

            when(mapper.findFavoriteById(favId)).thenReturn(dto);

            AdFavoriteDTO res = service.getFavoriteDetail(favId);

            assertThat(res).isSameAs(dto);
            verify(mapper).findFavoriteById(favId);
        }

        @Test
        @DisplayName("없으면 null 반환 (현재 구현 기준)")
        void getFavoriteDetail_nullWhenNotFound() {
            Long favId = 404L;
            when(mapper.findFavoriteById(favId)).thenReturn(null);

            AdFavoriteDTO res = service.getFavoriteDetail(favId);

            assertThat(res).isNull();
            verify(mapper).findFavoriteById(favId);
        }
    }

    @Nested
    @DisplayName("existsFavorite()")
    class ExistsFavoriteTests {

        @Test
        @DisplayName("존재 true")
        void exists_true() {
            when(mapper.existsFavorite(7L, 77L)).thenReturn(true);

            boolean exists = service.existsFavorite(7L, 77L);

            assertThat(exists).isTrue();
            verify(mapper).existsFavorite(7L, 77L);
        }

        @Test
        @DisplayName("존재 false")
        void exists_false() {
            when(mapper.existsFavorite(7L, 999L)).thenReturn(false);

            boolean exists = service.existsFavorite(7L, 999L);

            assertThat(exists).isFalse();
            verify(mapper).existsFavorite(7L, 999L);
        }
    }
}
