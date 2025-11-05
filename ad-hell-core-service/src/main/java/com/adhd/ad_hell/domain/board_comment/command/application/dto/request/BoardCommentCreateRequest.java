package com.adhd.ad_hell.domain.board_comment.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCommentCreateRequest {

    private Long writerId;   // 작성자 (User FK)
    private Long boardId;    // 게시글 (Board FK)
    private String content;  // 댓글 내용
}
