package com.adhd.ad_hell.domain.board_comment.query.mapper;

import com.adhd.ad_hell.domain.board_comment.query.dto.request.BoardCommentSearchRequest;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentDetailResponse;
import com.adhd.ad_hell.domain.board_comment.query.dto.response.BoardCommentSummaryResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardCommentMapper {

    List<BoardCommentSummaryResponse> findAllBoardComments(BoardCommentSearchRequest req);

    List<BoardCommentSummaryResponse> findMyComments(BoardCommentSearchRequest req);

    BoardCommentDetailResponse findCommentById(@Param("id") Long id);

    long countComments(BoardCommentSearchRequest req);
}
