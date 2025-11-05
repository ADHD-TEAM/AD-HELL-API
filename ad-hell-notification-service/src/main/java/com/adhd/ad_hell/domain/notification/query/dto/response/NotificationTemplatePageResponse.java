package com.adhd.ad_hell.domain.notification.query.dto.response;

import com.adhd.ad_hell.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;
import java.util.List;


@Getter
@Builder
public class NotificationTemplatePageResponse {

    private final List<NotificationTemplateSummaryResponse> templates;
    private final Pagination pagination;
}