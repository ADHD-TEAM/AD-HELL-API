package com.adhd.ad_hell.domain.board.query.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.board.query.dto.request.BoardSearchRequest;
import com.adhd.ad_hell.domain.board.query.dto.response.BoardDetailResponse;
import com.adhd.ad_hell.domain.board.query.dto.response.BoardListResponse;
import com.adhd.ad_hell.domain.board.query.service.BoardQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


  // 게시글 조회(QUERY) 컨트롤러
  // 목록: 검색 + 페이징 + 정렬
  // 상세: 조회수 증가/미증가 선택 가능

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardQueryController {

    private final BoardQueryService boardQueryService;


    // 게시글 목록 조회 (검색 + 페이징 + 정렬)
    // 예시: GET /api/boards?page=1&size=10&keyword=스프링&sortBy=viewCount&direction=DESC

    @GetMapping
    public ResponseEntity<ApiResponse<BoardListResponse>> getBoards(
            @ModelAttribute BoardSearchRequest request
    ) {
        BoardListResponse response = boardQueryService.getBoards(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // 게시글 상세 조회 (조회수 증가 O)
    // 예시: GET /api/boards/{boardId}

    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardDetailResponse>> getBoardAndIncreaseViewCount(
            @PathVariable Long boardId
    ) {
        BoardDetailResponse response = boardQueryService.getBoardAndIncreaseViewCount(boardId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // 게시글 상세 조회 (조회수 증가 X)
    // 예시: GET /api/boards/{boardId}/plain

    @GetMapping("/{boardId}/plain")
    public ResponseEntity<ApiResponse<BoardDetailResponse>> getBoardWithoutIncrease(
            @PathVariable Long boardId
    ) {
        BoardDetailResponse response = boardQueryService.getBoard(boardId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
