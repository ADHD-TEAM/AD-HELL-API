package com.adhd.ad_hell.domain.inquiry.query.mapper;

import com.adhd.ad_hell.domain.inquiry.query.dto.request.InquirySearchRequest;
import com.adhd.ad_hell.domain.inquiry.query.dto.response.InquiryDetailResponse;
import com.adhd.ad_hell.domain.inquiry.query.dto.response.InquirySummaryResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InquiryMapper {

    // 회원
    List<InquirySummaryResponse> findMyInquiries(@Param("req") InquirySearchRequest req);
    Long countMyInquiries(@Param("req") InquirySearchRequest req);
    InquiryDetailResponse findMyInquiryById(@Param("userId") Long userId, @Param("id") Long id);

    // 관리자
    List<InquirySummaryResponse> findAdminInquiries(@Param("req") InquirySearchRequest req);
    Long countAdminInquiries(@Param("req") InquirySearchRequest req);
    InquiryDetailResponse findAdminInquiryById(@Param("id") Long id);
}
