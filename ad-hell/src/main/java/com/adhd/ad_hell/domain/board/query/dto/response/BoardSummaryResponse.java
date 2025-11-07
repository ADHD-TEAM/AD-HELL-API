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
public class BoardSummaryResponse {

    private Long id;
    private String title;
    private String status;
    private Long viewCount;
    private String categoryName;
    private String writerName;
    private LocalDateTime createdAt;


}
