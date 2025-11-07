package com.adhd.ad_hell.domain.board_comment.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.request.BoardCommentCreateRequest;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.request.BoardCommentUpdateRequest;
import com.adhd.ad_hell.domain.board_comment.command.application.dto.response.BoardCommentCommandResponse;
import com.adhd.ad_hell.domain.board_comment.command.application.service.BoardCommentCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board_comments")
@RequiredArgsConstructor
@Tag(name = "Board Comment Command", description = "게시판 댓글 명령(Command) API")
public class BoardCommentCommandController {

    private final BoardCommentCommandService boardCommentCommandService;

    @Operation(
            summary = "댓글 등록",
            description = "게시글에 새로운 댓글을 등록한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "댓글 등록 성공"
            ),
    })
    @PostMapping
    public ResponseEntity<ApiResponse<BoardCommentCommandResponse>> createBoardComment(
            @RequestBody BoardCommentCreateRequest req
    ) {
        BoardCommentCommandResponse response = boardCommentCommandService.createBoardComment(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @Operation(
            summary = "댓글 수정",
            description = "기존에 작성된 댓글의 내용을 수정한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "댓글 수정 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "댓글을 찾을 수 없음"
            ),
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<BoardCommentCommandResponse>> updateBoardComment(
            @PathVariable Long commentId,
            @RequestBody BoardCommentUpdateRequest req
    ) {
        BoardCommentCommandResponse response = boardCommentCommandService.updateBoardComment(commentId, req);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(
            summary = "댓글 삭제",
            description = "작성자가 자신의 댓글을 삭제한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "댓글 삭제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "삭제 권한이 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "댓글을 찾을 수 없음"
            ),
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoardComment(
            @PathVariable Long commentId,
            @RequestParam Long writerId
    ) {
        boardCommentCommandService.deleteBoardComment(commentId, writerId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
