package com.adhd.ad_hell.domain.board.query.service;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.board.query.dto.request.BoardSearchRequest;
import com.adhd.ad_hell.domain.board.query.dto.response.BoardDetailResponse;
import com.adhd.ad_hell.domain.board.query.dto.response.BoardListResponse;
import com.adhd.ad_hell.domain.board.query.dto.response.BoardSummaryResponse;
import com.adhd.ad_hell.domain.board.query.mapper.BoardMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardQueryService {

    private final BoardMapper boardMapper;

    @Value("${file.base-url}")
    private String fileBaseUrl; // 예: http://localhost:8080/api/files/

    /** 게시글 목록 조회 (검색 + 페이징 + 정렬 + 파일 URL 포함) */
    public BoardListResponse getBoards(BoardSearchRequest request) {
        List<BoardSummaryResponse> boards = boardMapper.findAllBoards(request, fileBaseUrl);
        long totalItems = boardMapper.countAllBoards(request);

        int page = request.getPage();
        int size = request.getSize();

        return BoardListResponse.builder()
                .boards(boards)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPages((int) Math.ceil((double) totalItems / size))
                        .totalItems(totalItems)
                        .build())
                .build();
    }

    /** 게시글 상세 조회 (조회수 증가 O, 파일 URL 포함) */
    @Transactional
    public BoardDetailResponse getBoardAndIncreaseViewCount(Long boardId) {
        int updated = boardMapper.increaseViewCount(boardId);
        if (updated == 0) throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);

        return Optional.ofNullable(boardMapper.findBoardDetailById(boardId, fileBaseUrl))
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));
    }
}
