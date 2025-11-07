package com.adhd.ad_hell.domain.board_comment.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCommentUpdateRequest {

    private Long writerId;   // 수정자 (본인 검증용)
    private String content;  // 수정 내용
}
