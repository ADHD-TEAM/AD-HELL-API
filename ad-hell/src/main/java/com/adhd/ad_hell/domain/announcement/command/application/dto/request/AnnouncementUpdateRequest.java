package com.adhd.ad_hell.domain.announcement.command.application.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementUpdateRequest {
    private String title;
    private String content;
    private String status; // 선택 변경
}
