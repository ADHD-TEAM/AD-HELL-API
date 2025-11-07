package com.adhd.ad_hell.domain.board.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class BoardCommandResponse {

    private Long id;
    private String title;
    private Long writerId; //id
    private String content;
    private Long categoryId; //id
    private String status;
    private Long viewCount;

}


