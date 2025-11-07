package com.adhd.ad_hell.domain.board.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.board.query.dto.request.BoardSearchRequest;
import com.adhd.ad_hell.domain.board.query.dto.response.BoardDetailResponse;
import com.adhd.ad_hell.domain.board.query.dto.response.BoardListResponse;
import com.adhd.ad_hell.domain.board.query.service.BoardQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardQueryController {

    private final BoardQueryService boardQueryService;

    /** 게시글 목록 조회 (검색 + 페이징 + 정렬) */
    @GetMapping
    public ResponseEntity<ApiResponse<BoardListResponse>> getBoards(@ModelAttribute BoardSearchRequest request) {
        return ResponseEntity.ok(ApiResponse.success(boardQueryService.getBoards(request)));
    }

    /** 게시글 상세 조회 (조회수 증가 O) */
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardDetailResponse>> getBoardAndIncreaseViewCount(@PathVariable Long boardId) {
        return ResponseEntity.ok(ApiResponse.success(boardQueryService.getBoardAndIncreaseViewCount(boardId)));
    }

}
