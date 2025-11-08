package com.adhd.ad_hell.domain.ad_comment.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.adhd.ad_hell.domain.ad_comment.query.dto.request.AdCommentSearchRequest;
import com.adhd.ad_hell.domain.ad_comment.query.dto.response.AdCommentDetailResponse;
import com.adhd.ad_hell.domain.ad_comment.query.dto.response.AdCommentDto;
import com.adhd.ad_hell.domain.ad_comment.query.dto.response.AdCommentListResponse;
import com.adhd.ad_hell.domain.ad_comment.query.mapper.AdCommentMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdCommentQueryServiceTest {

    @Mock
    private AdCommentMapper adCommentMapper;

    @InjectMocks
    private AdCommentQueryService adCommentQueryService;

    @Test
    @DisplayName("광고 댓글 단건 조회 성공")
    void getComment_success() {
        Long commentId = 10L;
        AdCommentDto dto = AdCommentDto.builder()
                .adCommentId(commentId)
                .adId(100L)
                .userId(1L)
                .content("내용")
                .build();
        given(adCommentMapper.selectCommentById(commentId)).willReturn(dto);

        AdCommentDetailResponse res = adCommentQueryService.getComment(commentId);

        verify(adCommentMapper, times(1)).selectCommentById(commentId);
        assertThat(res).isNotNull();
        assertThat(res.getAdComment()).isNotNull();
        assertThat(res.getAdComment().getAdCommentId()).isEqualTo(commentId);
        assertThat(res.getAdComment().getContent()).isEqualTo("내용");
    }

    @Test
    @DisplayName("광고 댓글 단건 조회 실패 - 존재하지 않으면 예외")
    void getComment_notFound_throws() {
        Long commentId = 404L;
        given(adCommentMapper.selectCommentById(commentId)).willReturn(null);

        try {
            adCommentQueryService.getComment(commentId);
        } catch (BusinessException e) {
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.AD_COMMENT_NOT_FOUND);
            verify(adCommentMapper, times(1)).selectCommentById(commentId);
            return;
        }
        throw new AssertionError("예외가 발생해야 합니다.");
    }

    @Test
    @DisplayName("광고 댓글 목록 조회 성공 - 페이징 계산 포함")
    void getComments_success() {
        AdCommentSearchRequest req = new AdCommentSearchRequest();

        AdCommentDto d1 = AdCommentDto.builder()
                .adCommentId(1L)
                .adId(100L)
                .userId(10L)
                .content("c1")
                .build();

        AdCommentDto d2 = AdCommentDto.builder()
                .adCommentId(2L)
                .adId(100L)
                .userId(11L)
                .content("c2")
                .build();

        given(adCommentMapper.selectCommentsByAdId(req)).willReturn(List.of(d1, d2));
        given(adCommentMapper.countComments(req)).willReturn(27L);

        AdCommentListResponse res = adCommentQueryService.getComments(req);

        verify(adCommentMapper, times(1)).selectCommentsByAdId(req);
        verify(adCommentMapper, times(1)).countComments(req);

        assertThat(res).isNotNull();
        assertThat(res.getAdComments()).hasSize(2);
        assertThat(res.getAdComments().get(0).getContent()).isEqualTo("c1");
        assertThat(res.getAdComments().get(1).getContent()).isEqualTo("c2");
        assertThat(res.getPagination().getCurrentPage()).isEqualTo(1);
        assertThat(res.getPagination().getTotalItems()).isEqualTo(27L);
        assertThat(res.getPagination().getTotalPages()).isEqualTo(3);
    }

    @Test
    @DisplayName("내 광고 댓글 목록 조회 성공 - 페이징 계산 포함")
    void getMyComments_success() {
        AdCommentSearchRequest req = new AdCommentSearchRequest();

        AdCommentDto d1 = AdCommentDto.builder()
                .adCommentId(3L)
                .adId(200L)
                .userId(1L)
                .content("my1")
                .build();

        given(adCommentMapper.selectMyCommentsByUserId(req)).willReturn(List.of(d1));
        given(adCommentMapper.countMyComments(req)).willReturn(1L);

        AdCommentListResponse res = adCommentQueryService.getMyComments(req);

        verify(adCommentMapper, times(1)).selectMyCommentsByUserId(req);
        verify(adCommentMapper, times(1)).countMyComments(req);

        assertThat(res).isNotNull();
        assertThat(res.getAdComments()).hasSize(1);
        assertThat(res.getAdComments().get(0).getContent()).isEqualTo("my1");
        assertThat(res.getPagination().getCurrentPage()).isEqualTo(1);
        assertThat(res.getPagination().getTotalItems()).isEqualTo(1L);
        assertThat(res.getPagination().getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글수 증가 호출 성공 - Mapper 위임 검증")
    void increaseCommentCount_callsMapper() {
        Long adId = 10L;

        adCommentQueryService.increaseCommentCount(adId);

        verify(adCommentMapper, times(1)).incrementCommentCount(adId);
    }

    @Test
    @DisplayName("댓글수 감소 호출 성공 - Mapper 위임 검증")
    void decreaseCommentCount_callsMapper() {
        Long adId = 10L;

        adCommentQueryService.decreaseCommentCount(adId);

        verify(adCommentMapper, times(1)).decrementCommentCount(adId);
    }
}
