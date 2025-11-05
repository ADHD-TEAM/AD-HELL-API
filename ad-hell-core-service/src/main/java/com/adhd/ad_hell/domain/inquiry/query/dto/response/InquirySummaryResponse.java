package com.adhd.ad_hell.domain.inquiry.query.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquirySummaryResponse {

    private Long id;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String contentsSnippet; // 내용 일부만
    private String answered;        // Y / N
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
}
