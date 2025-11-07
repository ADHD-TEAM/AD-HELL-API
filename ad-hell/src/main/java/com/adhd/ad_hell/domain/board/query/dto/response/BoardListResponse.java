package com.adhd.ad_hell.domain.board.query.dto.response;

import com.adhd.ad_hell.common.dto.Pagination;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardListResponse {
    private List<BoardSummaryResponse> boards;
    private Pagination pagination;
}

