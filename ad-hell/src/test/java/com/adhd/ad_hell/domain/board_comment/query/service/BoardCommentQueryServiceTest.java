package com.adhd.ad_hell.domain.board_comment.query.service;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.board_comment.query.dto.request.BoardCommentSearchRequest;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentDetailResponse;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentListResponse;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentSummaryResponse;
import com.adhd.ad_hell.domain.board_comment.query.mapper.BoardCommentMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardCommentQueryServiceTest {

    @InjectMocks
    private BoardCommentQueryService boardCommentQueryService;

    @Mock
    private BoardCommentMapper boardCommentMapper;

    @Test
    @DisplayName("댓글 목록 조회 및 페이지네이션 계산 성공 테스트 (결과 여러 페이지)")
    void findAllBoardComments_success_multiplePages() {
        // given
        // 2페이지, 페이지당 10개 요청
        BoardCommentSearchRequest request = BoardCommentSearchRequest.builder()
                .page(2)
                .size(10)
                .build();

        // Mapper가 반환할 가짜 데이터 생성
        List<BoardCommentSummaryResponse> mockComments = List.of(
                BoardCommentSummaryResponse.builder().id(11L).content("댓글 11").build(),
                BoardCommentSummaryResponse.builder().id(12L).content("댓글 12").build()
        );
        long totalItems = 25L; // 총 25개의 댓글이 있다고 가정

        // Mock 동작 정의
        when(boardCommentMapper.findAllBoardComments(request)).thenReturn(mockComments);
        when(boardCommentMapper.countComments(request)).thenReturn(totalItems);

        // when
        BoardCommentListResponse response = boardCommentQueryService.findAllBoardComments(request);

        // then
        // 1. 반환된 댓글 목록 검증
        assertNotNull(response);
        assertEquals(mockComments, response.getComments());

        // 2. 페이지네이션 정보 검증
        Pagination pagination = response.getPagination();
        assertNotNull(pagination);
        assertEquals(2, pagination.getCurrentPage()); // 요청한 페이지
        assertEquals(totalItems, pagination.getTotalItems()); // 총 아이템 수
        assertEquals(3, pagination.getTotalPages()); // 총 페이지 수 (ceil(25.0 / 10.0) = 3)

        // 3. Mapper 메소드 호출 여부 검증
        verify(boardCommentMapper, times(1)).findAllBoardComments(request);
        verify(boardCommentMapper, times(1)).countComments(request);
    }

    @Test
    @DisplayName("내 댓글 목록 조회 성공 테스트")
    void findMyComments_success() {
        // given
        long writerId = 10L;
        int page = 2;
        int size = 5;
        long totalItems = 13L;

        // 서비스 메소드로 전달될 초기 요청 객체 (내부에서 재조립되는지 확인하기 위해 keyword 포함)
        BoardCommentSearchRequest initialRequest = BoardCommentSearchRequest.builder()
                .writerId(writerId)
                .page(page)
                .size(size)
                .keyword("무시될 키워드")
                .build();

        // Mapper가 반환할 가짜 데이터 생성
        List<BoardCommentSummaryResponse> mockComments = List.of(
                BoardCommentSummaryResponse.builder().id(6L).writerId(writerId).content("내 댓글 6").build(),
                BoardCommentSummaryResponse.builder().id(7L).writerId(writerId).content("내 댓글 7").build()
        );

        // Mapper에 전달되는 실제 request 객체를 캡처하기 위한 ArgumentCaptor
        ArgumentCaptor<BoardCommentSearchRequest> requestCaptor = ArgumentCaptor.forClass(BoardCommentSearchRequest.class);

        // Mock 동작 정의
        // .capture()를 사용하여 mapper 호출 시 사용된 request 객체를 잡아냅니다.
        when(boardCommentMapper.findMyComments(requestCaptor.capture())).thenReturn(mockComments);
        when(boardCommentMapper.countComments(requestCaptor.capture())).thenReturn(totalItems);

        // when
        ApiResponse<List<BoardCommentSummaryResponse>> apiResponse = boardCommentQueryService.findMyComments(initialRequest);

        // then
        // 1. API 응답의 데이터 부분 검증
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess()); // Lombok은 boolean 필드에 대해 isSuccess() getter를 생성
        assertEquals(mockComments, apiResponse.getData());

        // 참고: 현재 제공된 ApiResponse.success(data, pagination) 메소드는 pagination 정보를 응답 객체에 포함시키지 않습니다.
        // 따라서 pagination 관련 검증은 생략합니다.

        // 2. Mapper에 전달된 request 객체가 올바르게 재조립되었는지 검증
        List<BoardCommentSearchRequest> capturedRequests = requestCaptor.getAllValues();
        assertEquals(2, capturedRequests.size()); // findMyComments, countComments 두 번 캡처됨

        for (BoardCommentSearchRequest captured : capturedRequests) {
            assertEquals(writerId, captured.getWriterId());
            assertEquals(page, captured.getPage());
            assertEquals(size, captured.getSize());
            assertNull(captured.getKeyword(), "메소드 내부에서 새로 빌드되었으므로 keyword는 null이어야 합니다.");
        }
    }

    @Test
    @DisplayName("댓글 단건 조회 성공 테스트")
    void findCommentById_success() {
        // given
        long commentId = 1L;

        // Mapper가 반환할 가짜 상세 응답 객체 생성
        BoardCommentDetailResponse mockDetailResponse = BoardCommentDetailResponse.builder()
                .id(commentId)
                .writerId(10L)
                .writerName("testUser")
                .content("상세 내용입니다.")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();

        // boardCommentMapper.findCommentById가 호출될 때 mockDetailResponse를 반환하도록 설정
        when(boardCommentMapper.findCommentById(commentId)).thenReturn(mockDetailResponse);

        // when
        ApiResponse<BoardCommentDetailResponse> apiResponse = boardCommentQueryService.findCommentById(commentId);

        // then
        // 1. API 응답 구조 검증
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertNotNull(apiResponse.getTimestamp());

        // 2. 응답의 데이터가 Mapper가 반환한 객체와 동일한지 검증
        assertEquals(mockDetailResponse, apiResponse.getData());
        assertEquals(commentId, apiResponse.getData().getId());
        assertEquals("testUser", apiResponse.getData().getWriterName());

        // 3. Mapper 메소드가 올바른 인자로 1번 호출되었는지 검증
        verify(boardCommentMapper, times(1)).findCommentById(commentId);
    }
}