package com.adhd.ad_hell.domain.board_comment.query.dto.response;

import com.adhd.ad_hell.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardCommentListResponse {
    private final List<BoardCommentSummaryResponse> comments; // 목록
    private final Pagination pagination;                       // 페이징 메타
}
