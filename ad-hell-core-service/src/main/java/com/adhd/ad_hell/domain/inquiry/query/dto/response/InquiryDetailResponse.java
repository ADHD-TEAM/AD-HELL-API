package com.adhd.ad_hell.domain.inquiry.query.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDetailResponse {

    private Long id;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String contents;
    private String response;
    private String answered;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
}
