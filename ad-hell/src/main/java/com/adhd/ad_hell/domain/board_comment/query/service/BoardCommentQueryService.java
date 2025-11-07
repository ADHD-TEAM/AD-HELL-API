package com.adhd.ad_hell.domain.board_comment.query.service;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.board_comment.query.dto.request.BoardCommentSearchRequest;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentDetailResponse;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentListResponse;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentSummaryResponse;
import com.adhd.ad_hell.domain.board_comment.query.mapper.BoardCommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardCommentQueryService {

    private final BoardCommentMapper boardCommentMapper;

    /** 게시판 내 댓글 목록 (검색 + 페이징) */
    public BoardCommentListResponse findAllBoardComments(BoardCommentSearchRequest request) {
        // 1) 목록
        List<BoardCommentSummaryResponse> comments = boardCommentMapper.findAllBoardComments(request);

        // 2) 전체 건수
        long totalItems = boardCommentMapper.countComments(request);

        // 3) DTO에서 기본값(page=1, size=20) 보장 → 단순 대입
        int page = request.getPage();
        int size = request.getSize();

        // 4) 응답
        return BoardCommentListResponse.builder()
                .comments(comments)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPages((int) Math.ceil((double) totalItems / size))
                        .totalItems(totalItems)
                        .build())
                .build();
    }

    /** 내 댓글 조회 */
    public ApiResponse<List<BoardCommentSummaryResponse>> findMyComments(BoardCommentSearchRequest req) {
        int page = req.getPage();
        int size = req.getSize();

        req = BoardCommentSearchRequest.builder()
                .page(page)
                .size(size)
                .writerId(req.getWriterId())
                .build();

        List<BoardCommentSummaryResponse> comments = boardCommentMapper.findMyComments(req);
        long total = boardCommentMapper.countComments(req);

        Pagination pagination = Pagination.builder()
                .currentPage(page)
                .totalPages((int) Math.ceil((double) total / size))
                .totalItems(total)
                .build();

        return ApiResponse.success(comments, pagination);
    }

    /** 단건 조회 */
    public ApiResponse<BoardCommentDetailResponse> findCommentById(Long id) {
        BoardCommentDetailResponse comment = boardCommentMapper.findCommentById(id);
        return ApiResponse.success(comment);
    }
}
