package com.adhd.ad_hell.domain.advertise.query.service;

import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.advertise.query.dto.request.AdSearchRequest;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdDetailResponse;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdDto;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdListResponse;
import com.adhd.ad_hell.domain.advertise.query.mapper.AdMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdQueryService {

    private final AdMapper adMapper;

    // 광고 상세 조회
    @Transactional(readOnly = true)
    public AdDetailResponse getAd(Long adId) {
        // 조회수 증가
        adMapper.incrementViewCount(adId);

        AdDto ad = Optional.ofNullable(
                adMapper.selectAdById(adId))
                        .orElseThrow(() -> new BusinessException(ErrorCode.AD_NOT_FOUND))

        ;
        // 필요 시 null 처리 로직(예: 예외) 추가
        return AdDetailResponse.builder()
                .ad(ad)
                .build();
}
    @Transactional(readOnly = true)
    public AdListResponse getAds(AdSearchRequest adSearchRequest){
        // 필요한 컨텐츠 조회
        List<AdDto> ads = adMapper.selectAds(adSearchRequest);
        // 해당 검색 조건으로 총 몇개의 컨텐츠가 있는지 조회 (페이징을 위한 속성 값 계산에 필요)
        long totalItems = adMapper.countAds(adSearchRequest);

        int page = adSearchRequest.getPage();
        int size = adSearchRequest.getSize();

        return AdListResponse.builder()
                .ads(ads)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPages((int) Math.ceil((double) totalItems / size))
                        .totalItems(totalItems)
                        .build())
                .build();
    }
}
