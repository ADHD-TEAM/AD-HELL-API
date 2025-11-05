package com.adhd.ad_hell.domain.board_comment.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class BoardCommentDetailResponse {
    private Long id;
    private Long boardId;
    private Long writerId;
    private String writerName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
