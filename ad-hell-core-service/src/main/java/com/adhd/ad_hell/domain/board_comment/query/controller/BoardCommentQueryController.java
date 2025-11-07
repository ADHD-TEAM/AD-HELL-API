package com.adhd.ad_hell.domain.board_comment.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.board_comment.query.dto.request.BoardCommentSearchRequest;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentDetailResponse;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentListResponse;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentSummaryResponse;
import com.adhd.ad_hell.domain.board_comment.query.service.BoardCommentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board_comments")
@RequiredArgsConstructor
public class BoardCommentQueryController {

    private final BoardCommentQueryService boardCommentQueryService;

    /** 게시판 내 댓글 목록 조회 */
    @GetMapping
    public ResponseEntity<BoardCommentListResponse> findAllBoardComments(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long boardId,
            @RequestParam(required = false) String keyword
    ) {
        BoardCommentSearchRequest req = BoardCommentSearchRequest.builder()
                .page(page)
                .size(size)
                .boardId(boardId)
                .keyword(keyword)
                .build();

        // 서비스가 BoardCommentListResponse를 반환하므로 그대로 감싸서 반환
        return ResponseEntity.ok(boardCommentQueryService.findAllBoardComments(req));
    }

    /** 내 댓글 조회 */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BoardCommentSummaryResponse>>> findMyComments(
            @RequestParam Long writerId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        BoardCommentSearchRequest req = BoardCommentSearchRequest.builder()
                .page(page)
                .size(size)
                .writerId(writerId)
                .build();

        return ResponseEntity.ok(boardCommentQueryService.findMyComments(req));
    }

    /** 댓글 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardCommentDetailResponse>> findCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(boardCommentQueryService.findCommentById(id));
    }
}
