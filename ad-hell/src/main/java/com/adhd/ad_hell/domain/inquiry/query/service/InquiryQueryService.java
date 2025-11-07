package com.adhd.ad_hell.domain.inquiry.query.service;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.inquiry.query.dto.request.InquirySearchRequest;
import com.adhd.ad_hell.domain.inquiry.query.dto.response.InquiryDetailResponse;
import com.adhd.ad_hell.domain.inquiry.query.dto.response.InquiryListResponse;
import com.adhd.ad_hell.domain.inquiry.query.dto.response.InquirySummaryResponse;
import com.adhd.ad_hell.domain.inquiry.query.mapper.InquiryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor

// InquiryQueryService (필터/정렬/페이징 계산) + Mapper 호출 기능 담당
public class InquiryQueryService {

    private final InquiryMapper inquiryMapper;

    /** 회원 - 내 문의 목록 (검색/필터/페이징) */
    public InquiryListResponse getMyInquiries(InquirySearchRequest req) {
        List<InquirySummaryResponse> list = inquiryMapper.findMyInquiries(req);
        long totalItems = inquiryMapper.countMyInquiries(req);

        int page = req.getPage();
        int size = req.getSize();

        Pagination pagination = Pagination.builder()
                .currentPage(page)
                .totalPages((int) Math.ceil((double) totalItems / size))
                .totalItems(totalItems)
                .build();

        return InquiryListResponse.builder()
                .inquiries(list)
                .pagination(pagination)
                .build();
    }

    /** 회원 - 내 문의 상세 (userId 스코프 보장) */
    public InquiryDetailResponse getMyInquiryById(Long userId, Long id) {
        return inquiryMapper.findMyInquiryById(userId, id);
    }

    /** 관리자 - 전체 문의 목록 (검색/필터/페이징) */
    public InquiryListResponse getAdminInquiries(InquirySearchRequest req) {
        List<InquirySummaryResponse> list = inquiryMapper.findAdminInquiries(req);
        long totalItems = inquiryMapper.countAdminInquiries(req);

        int page = req.getPage();
        int size = req.getSize();

        Pagination pagination = Pagination.builder()
                .currentPage(page)
                .totalPages((int) Math.ceil((double) totalItems / size))
                .totalItems(totalItems)
                .build();

        return InquiryListResponse.builder()
                .inquiries(list)
                .pagination(pagination)
                .build();
    }

    /** 관리자 - 문의 상세 */
    public InquiryDetailResponse getAdminInquiryById(Long id) {
        return inquiryMapper.findAdminInquiryById(id);
    }
}