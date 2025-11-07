package com.adhd.ad_hell.domain.inquiry.query.dto.response;

import com.adhd.ad_hell.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class InquiryListResponse {
    private final List<InquirySummaryResponse> inquiries;
    private final Pagination pagination;
}
