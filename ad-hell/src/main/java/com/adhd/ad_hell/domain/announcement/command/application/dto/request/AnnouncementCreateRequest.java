package com.adhd.ad_hell.domain.announcement.command.application.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementCreateRequest {
    private Long writerId;
    private String title;
    private String content;
    private String status; // null이면 기본값 'Y'
}
