package com.adhd.ad_hell.domain.announcement.command.application.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementCommandResponse {
    private Long id;
    private String title;
    private String content;
    private String status;
}
