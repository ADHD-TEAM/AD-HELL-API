package com.adhd.ad_hell.domain.announcement.query.dto.response;

import com.adhd.ad_hell.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AnnouncementListResponse {
    private final List<AnnouncementSummaryResponse> announcements;
    private final Pagination pagination;
}