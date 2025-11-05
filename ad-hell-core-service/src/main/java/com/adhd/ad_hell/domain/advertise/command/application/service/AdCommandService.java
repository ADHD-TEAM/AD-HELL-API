package com.adhd.ad_hell.domain.advertise.command.application.service;

import com.adhd.ad_hell.common.storage.FileStorage;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdCreateRequest;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdUpdateRequest;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdFile;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdStatus;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdCommandService {

    private final AdRepository adRepository;
    private final FileStorage fileStorage;

    @Transactional
    public Long createAd(AdCreateRequest req) {
        Ad ad = Ad.fromCreateDto(req);
        adRepository.save(ad);
        return ad.getAdId();
    }

    @Transactional
    public void deleteAd(AdCreateRequest req) {
        adRepository.deleteById(req.getAdId());
    }

    /** 광고 수정 (메타데이터) */
    @Transactional
    public void updateAd(AdUpdateRequest req) {
        Ad ad = adRepository.findById(req.getAdId())
                .orElseThrow(() -> new BusinessException(ErrorCode.AD_NOT_FOUND));

        ad.updateAd(
                req.getTitle(),
                req.getLike_count(),
                req.getBookmark_count(),
                req.getComment_count(),
                req.getView_count()
        );
    }
}
