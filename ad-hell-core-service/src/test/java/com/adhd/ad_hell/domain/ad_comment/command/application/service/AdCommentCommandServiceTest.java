package com.adhd.ad_hell.domain.ad_comment.command.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.adhd.ad_hell.domain.ad_comment.command.application.dto.request.AdCommentCreateRequest;
import com.adhd.ad_hell.domain.ad_comment.command.application.dto.request.AdCommentUpdateRequest;
import com.adhd.ad_hell.domain.ad_comment.command.domain.aggregate.AdComment;
import com.adhd.ad_hell.domain.ad_comment.command.domain.repository.AdCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdCommentCommandServiceTest {

    @Mock
    private AdCommentRepository adCommentRepository;

    @InjectMocks
    private AdCommentCommandService adCommentCommandService;

    @Captor
    private ArgumentCaptor<AdComment> adCommentCaptor;

    @Test
    @DisplayName("광고 댓글 등록 성공 - 요청 DTO를 기반으로 저장 호출")
    void createAdComment_success() {
        // given
        AdCommentCreateRequest req = new AdCommentCreateRequest(1L, 10L, "첫 댓글");
        given(adCommentRepository.save(any(AdComment.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        adCommentCommandService.createAdComment(req);

        // then
        verify(adCommentRepository, times(1)).save(adCommentCaptor.capture());
        AdComment saved = adCommentCaptor.getValue();
        assertThat(saved.getUserId()).isEqualTo(10L);
        assertThat(saved.getAdId()).isEqualTo(1L);
        assertThat(saved.getContent()).isEqualTo("첫 댓글");
    }

    @Test
    @DisplayName("광고 댓글 수정 성공 - findById로 조회 후 내용 업데이트")
    void updateAdComment_success() {
        // given
        Long commentId = 100L;
        AdComment existing = AdComment.builder()
                .userId(1L)
                .adId(10L)
                .content("이전 내용")
                .build();
        given(adCommentRepository.findById(commentId)).willReturn(Optional.of(existing));

        AdCommentUpdateRequest req = new AdCommentUpdateRequest("수정된 내용");

        // when
        adCommentCommandService.updateAdComment(commentId, req);

        // then
        verify(adCommentRepository, times(1)).findById(commentId);
        assertThat(existing.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("광고 댓글 수정 실패 - 대상 미존재 시 예외 발생")
    void updateAdComment_notFound() {
        // given
        Long commentId = 404L;
        given(adCommentRepository.findById(commentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adCommentCommandService.updateAdComment(commentId, new AdCommentUpdateRequest("x")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("AdComment not found");
        verify(adCommentRepository, times(1)).findById(commentId);
    }

    @Test
    @DisplayName("광고 댓글 삭제 성공 - 존재 확인 후 삭제 호출")
    void deleteAdComment_success() {
        // given
        Long commentId = 200L;
        given(adCommentRepository.existsById(commentId)).willReturn(true);

        // when
        adCommentCommandService.deleteAdComment(commentId);

        // then
        verify(adCommentRepository, times(1)).existsById(commentId);
        verify(adCommentRepository, times(1)).deleteById(commentId);
    }

    @Test
    @DisplayName("광고 댓글 삭제 실패 - 대상 미존재 시 예외 발생")
    void deleteAdComment_notFound() {
        // given
        Long commentId = 300L;
        given(adCommentRepository.existsById(commentId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> adCommentCommandService.deleteAdComment(commentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("AdComment not found");
        verify(adCommentRepository, times(1)).existsById(commentId);
        verify(adCommentRepository, never()).deleteById(anyLong());
    }
}
