package com.adhd.ad_hell.domain.announcement.query.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String writerName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
