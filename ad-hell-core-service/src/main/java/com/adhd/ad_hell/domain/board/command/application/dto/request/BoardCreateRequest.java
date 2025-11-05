package com.adhd.ad_hell.domain.board.command.application.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor

public class BoardCreateRequest {

    @NotBlank
    private final String title;

    @NotBlank
    private final String content;

    @NotNull
    private final Long writerId;

    @NotNull
    private final Long categoryId;

    @NotBlank
    private final String status;

}
