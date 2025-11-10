package com.adhd.ad_hell.domain.inquiry.query.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.inquiry.query.dto.request.InquirySearchRequest;
import com.adhd.ad_hell.domain.inquiry.query.dto.response.InquiryDetailResponse;
import com.adhd.ad_hell.domain.inquiry.query.dto.response.InquiryListResponse;
import com.adhd.ad_hell.domain.inquiry.query.dto.response.InquirySummaryResponse;
import com.adhd.ad_hell.domain.inquiry.query.mapper.InquiryMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InquiryQueryServiceTest {

    @Mock private InquiryMapper inquiryMapper;
    @InjectMocks private InquiryQueryService inquiryQueryService;

    @Nested
    @DisplayName("getMyInquiries()")
    class GetMyInquiries {

        @Test
        @DisplayName("페이지네이션 계산 및 목록 매핑 성공")
        void success() {
            // given
            InquirySearchRequest req = InquirySearchRequest.builder()
                    .userId(1L)
                    .page(2)      // 2페이지
                    .size(20)     // 페이지당 20
                    .keyword("테스트")
                    .answered("Y")
                    .build();

            List<InquirySummaryResponse> list = List.of(
                    InquirySummaryResponse.builder()
                            .id(101L).userId(1L).categoryId(10L)
                            .categoryName("결제")
                            .title("카드 결제 오류")
                            .contentsSnippet("오류가 발생합니다...")
                            .answered("Y")
                            .answeredAt(LocalDateTime.now())
                            .createdAt(LocalDateTime.now())
                            .build(),
                    InquirySummaryResponse.builder()
                            .id(102L).userId(1L).categoryId(11L)
                            .categoryName("배송")
                            .title("배송 지연 문의")
                            .contentsSnippet("언제 오나요...")
                            .answered("Y")
                            .answeredAt(LocalDateTime.now())
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            long totalItems = 45L; // 총 45건 → size=20이면 totalPages=3

            given(inquiryMapper.findMyInquiries(req)).willReturn(list);
            given(inquiryMapper.countMyInquiries(req)).willReturn(totalItems);

            // when
            InquiryListResponse res = inquiryQueryService.getMyInquiries(req);

            // then
            assertThat(res).isNotNull();
            assertThat(res.getInquiries()).hasSize(2);

            Pagination p = res.getPagination();
            assertThat(p).isNotNull();
            assertThat(p.getCurrentPage()).isEqualTo(2);
            assertThat(p.getTotalItems()).isEqualTo(45L);
            assertThat(p.getTotalPages()).isEqualTo(3);

            verify(inquiryMapper).findMyInquiries(req);
            verify(inquiryMapper).countMyInquiries(req);
        }
    }

    @Nested
    @DisplayName("getMyInquiryById()")
    class GetMyInquiryById {

        @Test
        @DisplayName("내 문의 상세 반환")
        void success() {
            // given
            Long userId = 1L;
            Long id = 101L;

            InquiryDetailResponse dto = InquiryDetailResponse.builder()
                    .id(id).userId(userId).categoryId(10L)
                    .categoryName("결제")
                    .title("카드 결제 오류")
                    .contents("상세 내용")
                    .response("조치 완료")
                    .answered("Y")
                    .answeredAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .build();

            given(inquiryMapper.findMyInquiryById(userId, id)).willReturn(dto);

            // when
            InquiryDetailResponse res = inquiryQueryService.getMyInquiryById(userId, id);

            // then
            assertThat(res).isNotNull();
            assertThat(res.getId()).isEqualTo(id);
            assertThat(res.getUserId()).isEqualTo(userId);
            verify(inquiryMapper).findMyInquiryById(userId, id);
        }

        @Test
        @DisplayName("없으면 null 반환(현재 서비스 로직 기준)")
        void notFound_returnsNull() {
            // given
            Long userId = 1L;
            Long id = 999L;
            given(inquiryMapper.findMyInquiryById(userId, id)).willReturn(null);

            // when
            InquiryDetailResponse res = inquiryQueryService.getMyInquiryById(userId, id);

            // then
            assertThat(res).isNull();
            verify(inquiryMapper).findMyInquiryById(userId, id);
        }
    }

    @Nested
    @DisplayName("getAdminInquiries()")
    class GetAdminInquiries {

        @Test
        @DisplayName("페이지네이션 계산 및 목록 매핑 성공")
        void success() {
            // given
            InquirySearchRequest req = InquirySearchRequest.builder()
                    .page(1)
                    .size(10)  // 작은 사이즈
                    .keyword("배송")
                    .answered("N")
                    .build();

            List<InquirySummaryResponse> list = List.of(
                    InquirySummaryResponse.builder()
                            .id(201L).userId(3L).categoryId(12L)
                            .categoryName("배송")
                            .title("배송지 변경 문의")
                            .contentsSnippet("배송지 변경하고 싶어요")
                            .answered("N")
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            long totalItems = 11L; // size=10 → totalPages=2

            given(inquiryMapper.findAdminInquiries(req)).willReturn(list);
            given(inquiryMapper.countAdminInquiries(req)).willReturn(totalItems);

            // when
            InquiryListResponse res = inquiryQueryService.getAdminInquiries(req);

            // then
            assertThat(res).isNotNull();
            assertThat(res.getInquiries()).hasSize(1);

            Pagination p = res.getPagination();
            assertThat(p.getCurrentPage()).isEqualTo(1);
            assertThat(p.getTotalItems()).isEqualTo(11L);
            assertThat(p.getTotalPages()).isEqualTo(2);

            verify(inquiryMapper).findAdminInquiries(req);
            verify(inquiryMapper).countAdminInquiries(req);
        }
    }

    @Nested
    @DisplayName("getAdminInquiryById()")
    class GetAdminInquiryById {

        @Test
        @DisplayName("문의 상세 반환")
        void success() {
            // given
            Long id = 300L;
            InquiryDetailResponse dto = InquiryDetailResponse.builder()
                    .id(id).userId(7L).categoryId(5L)
                    .categoryName("기타")
                    .title("기타 문의")
                    .contents("내용")
                    .response(null)
                    .answered("N")
                    .createdAt(LocalDateTime.now())
                    .build();

            given(inquiryMapper.findAdminInquiryById(id)).willReturn(dto);

            // when
            InquiryDetailResponse res = inquiryQueryService.getAdminInquiryById(id);

            // then
            assertThat(res).isNotNull();
            assertThat(res.getId()).isEqualTo(id);
            verify(inquiryMapper).findAdminInquiryById(id);
        }

        @Test
        @DisplayName("없으면 null 반환(현재 서비스 로직 기준)")
        void notFound_returnsNull() {
            // given
            Long id = 404L;
            given(inquiryMapper.findAdminInquiryById(id)).willReturn(null);

            // when
            InquiryDetailResponse res = inquiryQueryService.getAdminInquiryById(id);

            // then
            assertThat(res).isNull();
            verify(inquiryMapper).findAdminInquiryById(id);
        }
    }
}
