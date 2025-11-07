package com.adhd.ad_hell.domain.board_comment.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class BoardCommentSummaryResponse {
    private Long id;
    private Long writerId;
    private String writerName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
