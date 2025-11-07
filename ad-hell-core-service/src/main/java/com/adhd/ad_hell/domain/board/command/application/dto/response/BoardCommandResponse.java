package com.adhd.ad_hell.domain.board.command.application.dto.response;

import com.adhd.ad_hell.domain.user.command.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


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

    //생성자를 만드는 것보다 @builder 주입을 추천
   /* public BoardCommandResponse(Long id, String title, String content, String status, Long viewCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
        this.viewCount = viewCount;
    }*/
}


