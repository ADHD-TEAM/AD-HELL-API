package com.adhd.ad_hell.domain.announcement.query.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.announcement.query.dto.request.AnnouncementSearchRequest;
import com.adhd.ad_hell.domain.announcement.query.dto.response.AnnouncementDetailResponse;
import com.adhd.ad_hell.domain.announcement.query.dto.response.AnnouncementListResponse;
import com.adhd.ad_hell.domain.announcement.query.dto.response.AnnouncementSummaryResponse;
import com.adhd.ad_hell.domain.announcement.query.mapper.AnnouncementMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnnouncementQueryServiceTest {

    @Mock
    private AnnouncementMapper mapper;

    @InjectMocks
    private AnnouncementQueryService service;

    @Nested
    @DisplayName("getAnnouncements()")
    class GetAnnouncements {

        @Test
        @DisplayName("공지 목록 조회 성공 - 기본 page/size 보정 및 Pagination 계산")
        void getAnnouncements_success() {
            // given
            AnnouncementSearchRequest req = new AnnouncementSearchRequest(); // 세터 호출 없음

            AnnouncementSummaryResponse s1 =
                    new AnnouncementSummaryResponse(1L, "공지1", "관리자", "Y", LocalDateTime.now());
            AnnouncementSummaryResponse s2 =
                    new AnnouncementSummaryResponse(2L, "공지2", "관리자", "Y", LocalDateTime.now());

            when(mapper.findAllAnnouncements(any(AnnouncementSearchRequest.class)))
                    .thenReturn(List.of(s1, s2));
            when(mapper.countAllAnnouncements(any(AnnouncementSearchRequest.class)))
                    .thenReturn(2L);

            // when
            AnnouncementListResponse result = service.getAnnouncements(req);

            // then
            assertThat(result.getAnnouncements()).hasSize(2);
            Pagination p = result.getPagination();
            assertThat(p.getTotalItems()).isEqualTo(2L);
            assertThat(p.getCurrentPage()).isEqualTo(1);


            verify(mapper).findAllAnnouncements(any(AnnouncementSearchRequest.class));
            verify(mapper).countAllAnnouncements(any(AnnouncementSearchRequest.class));
        }

        @Test
        @DisplayName("공지 0건일 때도 빈 리스트 + total=0 반환")
        void getAnnouncements_empty() {
            AnnouncementSearchRequest req = new AnnouncementSearchRequest();

            when(mapper.findAllAnnouncements(any(AnnouncementSearchRequest.class)))
                    .thenReturn(List.of());
            when(mapper.countAllAnnouncements(any(AnnouncementSearchRequest.class)))
                    .thenReturn(0L);

            AnnouncementListResponse res = service.getAnnouncements(req);

            assertThat(res.getAnnouncements()).isEmpty();
            assertThat(res.getPagination().getTotalItems()).isZero();

            verify(mapper).findAllAnnouncements(any(AnnouncementSearchRequest.class));
            verify(mapper).countAllAnnouncements(any(AnnouncementSearchRequest.class));
        }
    }

    @Nested
    @DisplayName("getAnnouncementDetail()")
    class GetAnnouncementDetail {

        @Test
        @DisplayName("단건 조회 성공")
        void detail_success() {
            Long id = 1L;
            AnnouncementDetailResponse dto = AnnouncementDetailResponse.builder()
                    .id(id)
                    .title("공지 상세")
                    .content("내용입니다")
                    .writerName("관리자")
                    .status("Y")
                    .createdAt(LocalDateTime.now())
                    .build();

            when(mapper.findAnnouncementDetailById(id)).thenReturn(dto);

            AnnouncementDetailResponse result = service.getAnnouncementDetail(id);

            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("공지 상세");
            verify(mapper).findAnnouncementDetailById(id);
        }

        @Test
        @DisplayName("존재하지 않으면 ANNOUNCEMENT_NOT_FOUND 발생")
        void detail_notFound() {
            Long id = 999L;
            when(mapper.findAnnouncementDetailById(id)).thenReturn(null);

            assertThatThrownBy(() -> service.getAnnouncementDetail(id))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.ANNOUNCEMENT_NOT_FOUND.getMessage());

            verify(mapper).findAnnouncementDetailById(id);
        }
    }
}
