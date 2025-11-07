package com.adhd.ad_hell.domain.board_comment.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.board_comment.query.dto.request.BoardCommentSearchRequest;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentDetailResponse;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentListResponse;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentSummaryResponse;
import com.adhd.ad_hell.domain.board_comment.query.service.BoardCommentQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board_comments")
@RequiredArgsConstructor
@Tag(name = "Board Comment Query", description = "게시판 댓글 조회 API")
public class BoardCommentQueryController {

    private final BoardCommentQueryService boardCommentQueryService;

    @Operation(
            summary = "게시판 댓글 목록 조회",
            description = "특정 게시판 내 댓글 목록을 검색 및 페이징하여 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "댓글 목록 조회 성공"
            ),
    })
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

        return ResponseEntity.ok(boardCommentQueryService.findAllBoardComments(req));
    }

    @Operation(
            summary = "내 댓글 조회",
            description = "작성자 ID로 내가 작성한 댓글 목록을 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "내 댓글 목록 조회 성공"
            ),
    })
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

    @Operation(
            summary = "댓글 상세 조회",
            description = "댓글 ID로 상세 정보를 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "댓글 상세 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "해당 댓글을 찾을 수 없음"
            ),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardCommentDetailResponse>> findCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(boardCommentQueryService.findCommentById(id));
    }
}