package com.adhd.ad_hell.domain.advertise.query.service;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.advertise.query.dto.request.AdCommentSearchRequest;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdCommentDetailResponse;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdCommentDto;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdCommentListResponse;
import com.adhd.ad_hell.domain.advertise.query.mapper.AdCommentMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdCommentQueryService {

    private final AdCommentMapper adCommentMapper;

    // 광고 댓글 단건 조회
    @Transactional(readOnly = true)
    public AdCommentDetailResponse getComment(Long commentId) {
        AdCommentDto comment = Optional.ofNullable(
                adCommentMapper.selectCommentById(commentId)
        ).orElseThrow(() -> new BusinessException(ErrorCode.AD_COMMENT_NOT_FOUND));

        return AdCommentDetailResponse.builder()
                .adComment(comment)
                .build();
    }

    // 광고 댓글 목록 조회 (페이징)
    @Transactional(readOnly = true)
    public AdCommentListResponse getComments(AdCommentSearchRequest request) {
        List<AdCommentDto> comments = adCommentMapper.selectCommentsByAdId(request);
        long totalItems = adCommentMapper.countComments(request);

        int page = request.getPage();
        int size = request.getSize();

        return AdCommentListResponse.builder()
                .adComments(comments)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPages((int) Math.ceil((double) totalItems / size))
                        .totalItems(totalItems)
                        .build())
                .build();
    }

    // 내 광고 댓글 목록 조회 (페이징)
    @Transactional(readOnly = true)
    public AdCommentListResponse getMyComments(AdCommentSearchRequest request) {
        List<AdCommentDto> comments = adCommentMapper.selectMyCommentsByUserId(request);
        long totalItems = adCommentMapper.countMyComments(request);

        int page = request.getPage();
        int size = request.getSize();

        return AdCommentListResponse.builder()
                .adComments(comments)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPages((int) Math.ceil((double) totalItems / size))
                        .totalItems(totalItems)
                        .build())
                .build();
    }

    // 댓글수 증가 (댓글 생성 후 호출)
    @Transactional
    public void increaseCommentCount(Long adId) {
        adCommentMapper.incrementCommentCount(adId);
    }

    // 댓글수 감소 (댓글 삭제 후 호출)
    @Transactional
    public void decreaseCommentCount(Long adId) {
        adCommentMapper.decrementCommentCount(adId);
    }

}
