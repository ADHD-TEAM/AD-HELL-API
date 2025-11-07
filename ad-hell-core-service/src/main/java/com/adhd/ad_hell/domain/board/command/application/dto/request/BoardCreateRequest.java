package com.adhd.ad_hell.domain.board.command.application.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCreateRequest {

    @NotBlank
    private  String title;

    @NotBlank
    private  String content;

    private  Long writerId;

    @NotNull
    private  Long categoryId;

    @NotBlank
    private  String status;

    private  String imageUrl;

}
