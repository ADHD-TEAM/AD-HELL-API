package com.adhd.ad_hell.domain.announcement.query.mapper;

import com.adhd.ad_hell.domain.announcement.query.dto.request.AnnouncementSearchRequest;
import com.adhd.ad_hell.domain.announcement.query.dto.response.AnnouncementDetailResponse;
import com.adhd.ad_hell.domain.announcement.query.dto.response.AnnouncementSummaryResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnnouncementMapper {

    // 공지사항 목록 조회 (검색 + 페이징 + 정렬)
    List<AnnouncementSummaryResponse> findAllAnnouncements(@Param("request") AnnouncementSearchRequest request);

    // 목록 총 개수 (페이징 total 계산용)
    long countAllAnnouncements(@Param("request") AnnouncementSearchRequest request);

    // 공지사항 상세 조회
    AnnouncementDetailResponse findAnnouncementDetailById(@Param("announcementId") Long announcementId);
}
