package com.adhd.ad_hell.domain.board.query.service;

import com.adhd.ad_hell.domain.board.query.dto.request.BoardSearchRequest;
import com.adhd.ad_hell.domain.board.query.dto.response.BoardDetailResponse;
import com.adhd.ad_hell.domain.board.query.dto.response.BoardListResponse;
import com.adhd.ad_hell.domain.board.query.dto.response.BoardSummaryResponse;
import com.adhd.ad_hell.domain.board.query.mapper.BoardMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BoardQueryServiceTest {

    @Mock
    private BoardMapper boardMapper;

    @InjectMocks
    private BoardQueryService boardQueryService;

    private final String fileBaseUrl = "http://localhost:8080/api/files/";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // @Value 로 주입되는 필드 설정
        ReflectionTestUtils.setField(boardQueryService, "fileBaseUrl", fileBaseUrl);
    }

    @Test
    @DisplayName("게시글 목록 조회 - 페이징과 총합 계산이 올바르게 동작한다")
    void getBoards_success_withPagination() {
        // given
        BoardSearchRequest request = BoardSearchRequest.builder()
                .page(2)
                .size(3)
                .keyword("test")
                .build();

        List<BoardSummaryResponse> summaries = List.of(
                BoardSummaryResponse.builder().id(1L).title("t1").status("ACTIVE").viewCount(10L).categoryName("cat").writerName("w").createdAt(LocalDateTime.now()).build(),
                BoardSummaryResponse.builder().id(2L).title("t2").status("ACTIVE").viewCount(11L).categoryName("cat").writerName("w").createdAt(LocalDateTime.now()).build(),
                BoardSummaryResponse.builder().id(3L).title("t3").status("ACTIVE").viewCount(12L).categoryName("cat").writerName("w").createdAt(LocalDateTime.now()).build()
        );

        when(boardMapper.findAllBoards(eq(request), eq(fileBaseUrl))).thenReturn(summaries);
        when(boardMapper.countAllBoards(eq(request))).thenReturn(8L); // totalPages = ceil(8/3) = 3

        // when
        BoardListResponse response = boardQueryService.getBoards(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getPagination());
        assertEquals(3, response.getBoards().size());
        assertEquals(2, response.getPagination().getCurrentPage());
        assertEquals(3, response.getPagination().getTotalPages());
        assertEquals(8L, response.getPagination().getTotalItems());

        verify(boardMapper, times(1)).findAllBoards(eq(request), eq(fileBaseUrl));
        verify(boardMapper, times(1)).countAllBoards(eq(request));
        verifyNoMoreInteractions(boardMapper);
    }

    @Test
    @DisplayName("게시글 상세 조회 - 조회수 증가 후 상세 반환")
    void getBoardAndIncreaseViewCount_success() {
        // given
        Long boardId = 100L;
        when(boardMapper.increaseViewCount(boardId)).thenReturn(1);

        BoardDetailResponse detail = BoardDetailResponse.builder()
                .id(boardId)
                .title("title")
                .content("content")
                .imageUrl(fileBaseUrl + "image.png")
                .status("ACTIVE")
                .viewCount(123L)
                .categoryId(1L)
                .categoryName("공지")
                .writerId(10L)
                .writerName("작성자")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(boardMapper.findBoardDetailById(eq(boardId), eq(fileBaseUrl))).thenReturn(detail);

        // when
        BoardDetailResponse response = boardQueryService.getBoardAndIncreaseViewCount(boardId);

        // then
        assertNotNull(response);
        assertEquals(boardId, response.getId());
        assertEquals("title", response.getTitle());

        verify(boardMapper, times(1)).increaseViewCount(boardId);
        verify(boardMapper, times(1)).findBoardDetailById(eq(boardId), eq(fileBaseUrl));
        verifyNoMoreInteractions(boardMapper);
    }

    @Test
    @DisplayName("게시글 상세 조회 - 조회수 증가 실패 시 BOARD_NOT_FOUND 예외")
    void getBoardAndIncreaseViewCount_notFound_whenIncreaseFailed() {
        // given
        Long boardId = 200L;
        when(boardMapper.increaseViewCount(boardId)).thenReturn(0);

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> boardQueryService.getBoardAndIncreaseViewCount(boardId));

        assertEquals(ErrorCode.BOARD_NOT_FOUND, ex.getErrorCode());
        verify(boardMapper, times(1)).increaseViewCount(boardId);
        verify(boardMapper, never()).findBoardDetailById(anyLong(), anyString());
        verifyNoMoreInteractions(boardMapper);
    }

    @Test
    @DisplayName("게시글 상세 조회 - 상세가 없으면 BOARD_NOT_FOUND 예외")
    void getBoardAndIncreaseViewCount_notFound_whenDetailNull() {
        // given
        Long boardId = 300L;
        when(boardMapper.increaseViewCount(boardId)).thenReturn(1);
        when(boardMapper.findBoardDetailById(eq(boardId), eq(fileBaseUrl))).thenReturn(null);

        // when & then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> boardQueryService.getBoardAndIncreaseViewCount(boardId));

        assertEquals(ErrorCode.BOARD_NOT_FOUND, ex.getErrorCode());
        verify(boardMapper, times(1)).increaseViewCount(boardId);
        verify(boardMapper, times(1)).findBoardDetailById(eq(boardId), eq(fileBaseUrl));
        verifyNoMoreInteractions(boardMapper);
    }
}
