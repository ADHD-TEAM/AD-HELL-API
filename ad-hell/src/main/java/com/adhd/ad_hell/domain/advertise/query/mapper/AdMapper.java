package com.adhd.ad_hell.domain.advertise.query.mapper;

import com.adhd.ad_hell.domain.advertise.query.dto.request.AdSearchRequest;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdMapper {

    /* 조회수 증가 */
    void incrementViewCount(Long adId);

    //광고 조회
    AdDto selectAdById(Long adId);
    /* 검색&페이징 적용한 상품 목록 조회 */
    List<AdDto> selectAds(AdSearchRequest adSearchRequest);
    /* 페이징 속성 계산을 위한 전체 컨텐츠 개수 조회 */
    long countAds(AdSearchRequest adSearchRequest);

}
