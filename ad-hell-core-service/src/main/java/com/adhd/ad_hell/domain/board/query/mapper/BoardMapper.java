package com.adhd.ad_hell.domain.board.query.mapper;

import com.adhd.ad_hell.domain.board.query.dto.request.BoardSearchRequest;
import com.adhd.ad_hell.domain.board.query.dto.response.BoardDetailResponse;
import com.adhd.ad_hell.domain.board.query.dto.response.BoardSummaryResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {

        // 게시글 목록 조회 (검색 + 페이징 + 정렬)
        List<BoardSummaryResponse> findAllBoards(@Param("request") BoardSearchRequest boardSearchRequest);

        // 목록 총 개수 (페이징 total 계산용)
        long countAllBoards(@Param("request")BoardSearchRequest boardSearchRequest);

        // 상세 조회(조인 포함) — 필요 시
        BoardDetailResponse findBoardDetailById(@Param("boardId")Long boardId);

        // 조회수 증가
        int increaseViewCount(@Param("boardId") Long boardId);

}