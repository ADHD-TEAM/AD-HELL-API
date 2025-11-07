package com.adhd.ad_hell.domain.advertise.command.application.service;

import com.adhd.ad_hell.common.storage.FileStorage;
import com.adhd.ad_hell.common.storage.FileStorageResult;
import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdCreateRequest;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdUpdateRequest;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdFile;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.FileType;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdCommandService {

    private final AdRepository adRepository;
    private final FileStorage fileStorage;
    private final SecurityUtil securityUtil;

    @Transactional
    public Long createAd(AdCreateRequest req, List<MultipartFile> videoFiles) {
        Long userId = securityUtil.getLoginUserInfo().getUserId();
        Ad ad = Ad.fromCreateDto(req, userId);
        adRepository.save(ad);

        List<String> storedNames = new ArrayList<>();

        if (videoFiles != null && !videoFiles.isEmpty()) {
            for (MultipartFile file : videoFiles) {
                if (file == null || file.isEmpty()) continue;

                FileStorageResult result = fileStorage.store(file);

                storedNames.add(result.getStoredName());
                AdFile adFile = AdFile.of(result.getStoredName(), file.getOriginalFilename(), FileType.VIDEO);
                ad.addFile(adFile);
            }
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    storedNames.forEach(fileStorage::deleteQuietly);
                }
            }
        });

        return ad.getAdId();
    }

    @Transactional
    public void updateAd(Long adId, AdUpdateRequest req) {
        Ad ad = adRepository.findById(adId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.AD_NOT_FOUND));

        ad.updateAdInfo(req.getTitle(), req.getCategoryId());
    }

    @Transactional
    public void deleteAd(Long adId) {
        Ad ad = adRepository.findById(adId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.AD_NOT_FOUND));

        adRepository.deleteById(adId);

        ad.getFiles().forEach(file -> fileStorage.deleteQuietly(file.getStoredName()));
    }
}
