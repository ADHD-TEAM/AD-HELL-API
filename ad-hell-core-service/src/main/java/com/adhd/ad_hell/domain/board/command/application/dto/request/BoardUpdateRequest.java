package com.adhd.ad_hell.domain.board.command.application.dto.request;


import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class BoardUpdateRequest {

    private final String title;
    private final String content;
    private final String status;
    private final Long categoryId;
}
