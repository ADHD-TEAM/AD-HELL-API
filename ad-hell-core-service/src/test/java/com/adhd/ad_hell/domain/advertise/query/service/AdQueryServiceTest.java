package com.adhd.ad_hell.domain.advertise.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
// ... existing code ...
import com.adhd.ad_hell.common.dto.Pagination;
import com.adhd.ad_hell.domain.advertise.query.dto.request.AdSearchRequest;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdDetailResponse;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdDto;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdFileDto;
import com.adhd.ad_hell.domain.advertise.query.dto.response.AdListResponse;
import com.adhd.ad_hell.domain.advertise.query.mapper.AdMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class AdQueryServiceTest {

    @Mock
    private AdMapper adMapper;

    @InjectMocks
    private AdQueryService adQueryService;

    @Test
    @DisplayName("광고 단건 조회 성공 - 조회수 증가 호출 및 파일 URL 세팅")
    void getAd_success_setsFileUrl_andIncrementsView() {
        // given
        ReflectionTestUtils.setField(adQueryService, "fileBaseUrl", "http://files/");
        Long adId = 1L;

        AdDto dto = new AdDto();
        dto.setAdId(adId);
        dto.setTitle("광고1");
        dto.setFiles(List.of(
                new AdFileDto(10L, "v1.mp4", "s1.mp4", null, "VIDEO"),
                new AdFileDto(11L, "v2.mp4", "s2.mp4", null, "VIDEO")
        ));

        given(adMapper.selectAdById(adId)).willReturn(dto);

        // when
        AdDetailResponse result = adQueryService.getAd(adId);

        // then
        verify(adMapper, times(1)).incrementViewCount(adId);
        verify(adMapper, times(1)).selectAdById(adId);

        assertThat(result).isNotNull();
        assertThat(result.getAd()).isNotNull();
        assertThat(result.getAd().getAdId()).isEqualTo(adId);
        assertThat(result.getAd().getFiles()).hasSize(2);
        assertThat(result.getAd().getFiles().get(0).getFileUrl()).isEqualTo("http://files/s1.mp4");
        assertThat(result.getAd().getFiles().get(1).getFileUrl()).isEqualTo("http://files/s2.mp4");
    }

    @Test
    @DisplayName("광고 단건 조회 실패 - 존재하지 않으면 예외 발생")
    void getAd_notFound_throws() {
        // given
        ReflectionTestUtils.setField(adQueryService, "fileBaseUrl", "http://files/");
        Long adId = 999L;
        given(adMapper.selectAdById(adId)).willReturn(null);

        // when
        try {
            adQueryService.getAd(adId);
        } catch (BusinessException e) {
            // then
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.AD_NOT_FOUND);
            verify(adMapper, times(1)).incrementViewCount(adId);
            verify(adMapper, times(1)).selectAdById(adId);
            return;
        }
        throw new AssertionError("예외가 발생해야 합니다.");
    }

    @Test
    @DisplayName("광고 목록 조회 성공 - 파일 URL 세팅 및 페이징 정보 계산")
    void getAds_success_setsFileUrls_andPagination() {
        // given
        ReflectionTestUtils.setField(adQueryService, "fileBaseUrl", "http://files/");
        AdSearchRequest req = new AdSearchRequest();
        // 기본 page=1, size=10 그대로 사용

        AdDto ad1 = new AdDto();
        ad1.setAdId(1L);
        ad1.setTitle("A1");
        ad1.setFiles(List.of(new AdFileDto(101L, "a1.mp4", "a1s.mp4", null, "VIDEO")));

        AdDto ad2 = new AdDto();
        ad2.setAdId(2L);
        ad2.setTitle("A2");
        ad2.setFiles(List.of(new AdFileDto(102L, "a2.mp4", "a2s.mp4", null, "VIDEO")));

        given(adMapper.selectAds(req)).willReturn(List.of(ad1, ad2));
        given(adMapper.countAds(req)).willReturn(27L); // totalItems

        // when
        AdListResponse result = adQueryService.getAds(req);

        // then
        verify(adMapper, times(1)).selectAds(req);
        verify(adMapper, times(1)).countAds(req);

        assertThat(result).isNotNull();
        assertThat(result.getAds()).hasSize(2);

        // 파일 URL 세팅 확인
        assertThat(result.getAds().get(0).getFiles().get(0).getFileUrl()).isEqualTo("http://files/a1s.mp4");
        assertThat(result.getAds().get(1).getFiles().get(0).getFileUrl()).isEqualTo("http://files/a2s.mp4");

        // 페이징 계산 확인: page=1, size=10, totalItems=27 -> totalPages=3
        Pagination p = result.getPagination();
        assertThat(p.getCurrentPage()).isEqualTo(1);
        assertThat(p.getTotalItems()).isEqualTo(27L);
        assertThat(p.getTotalPages()).isEqualTo(3);
    }
}
