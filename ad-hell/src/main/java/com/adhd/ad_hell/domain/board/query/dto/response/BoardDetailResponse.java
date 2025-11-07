package com.adhd.ad_hell.domain.board.query.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDetailResponse {

    // Board
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String status;
    private Long viewCount;

    // Category
    private Long categoryId;
    private String categoryName;

    // User
    private Long writerId;
    private String writerName;

    // 등록,수정 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
