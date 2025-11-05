package com.adhd.ad_hell.domain.board_comment.command.application.controller;


import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.request.BoardCommentCreateRequest;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.request.BoardCommentUpdateRequest;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.response.BoardCommentCommandResponse;
import com.adhd.ad_hell.domain.board_comment.command.application.service.BoardCommentCommandService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board_comments")
@RequiredArgsConstructor
public class BoardCommentController {


    private final BoardCommentCommandService boardCommentCommandService;

    /** 댓글 등록 */
    @PostMapping
    public ResponseEntity<ApiResponse<BoardCommentCommandResponse>> createBoardComment(
            @RequestBody BoardCommentCreateRequest req
    ) {
        var response = boardCommentCommandService.createBoardComment(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    /** 댓글 수정 */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<BoardCommentCommandResponse>> updateBoardComment(
            @PathVariable Long commentId,
            @RequestBody BoardCommentUpdateRequest req
    ) {
        var response = boardCommentCommandService.updateBoardComment(commentId, req);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 댓글 삭제 */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoardComment(
            @PathVariable Long commentId,
            @RequestParam Long writerId
    ) {
        boardCommentCommandService.deleteBoardComment(commentId, writerId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}