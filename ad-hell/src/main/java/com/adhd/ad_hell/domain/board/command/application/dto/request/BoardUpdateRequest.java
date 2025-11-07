package com.adhd.ad_hell.domain.board.command.application.dto.request;


import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardUpdateRequest {

    private  String title;
    private  String content;
    private  String status;
    private  Long categoryId;
}
