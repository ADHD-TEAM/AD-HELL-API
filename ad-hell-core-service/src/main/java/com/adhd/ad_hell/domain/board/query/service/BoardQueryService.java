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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardQueryService {

    private final BoardMapper boardMapper;

    /** 게시글 상세 조회 */
    public BoardDetailResponse getBoard(Long boardId) {
        return Optional.ofNullable(boardMapper.findBoardDetailById(boardId))
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));
    }

    /** 게시글 목록 조회 (검색 + 페이징 + 정렬) */
    public BoardListResponse getBoards(BoardSearchRequest request) {
        List<BoardSummaryResponse> boards = boardMapper.findAllBoards(request);
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

    @Transactional // 조회수 증가이므로 readOnly=false
    public BoardDetailResponse getBoardAndIncreaseViewCount(Long boardId) {
        // 1) 먼저 조회수 증가 (영향받은 행이 0이면 존재하지 않는 게시글)
        int updated = boardMapper.increaseViewCount(boardId);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.BOARD_NOT_FOUND);
        }

        // 2) 증가된 상태로 상세 조회 반환
        return Optional.ofNullable(boardMapper.findBoardDetailById(boardId))
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));
    }
}
