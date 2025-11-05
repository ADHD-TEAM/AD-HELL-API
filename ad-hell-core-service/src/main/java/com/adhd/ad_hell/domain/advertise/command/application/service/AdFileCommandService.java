package com.adhd.ad_hell.domain.advertise.command.application.service;

import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdFileCreateRequest;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdFile;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdFileRepository;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdRepository;
import com.adhd.ad_hell.common.storage.FileStorage;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdCreateRequest;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdUpdateRequest;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdFileCommandService {

    private final AdFileRepository adFileRepository;
    private final AdRepository adRepository;
    private final FileStorage fileStorage;

    @Value("${ad.adfile-url}")
    private String AdFile_URL;

    /* 광고 파일 등록 */
    @Transactional
    public void createAdFile(AdFileCreateRequest adFileCreateRequest, List<MultipartFile> adContents) {

        // 1) 연관 Ad 조회 (요청에서 adId를 받는다고 가정)
        Ad ad = adRepository.findById(adFileCreateRequest.getAdId())
                .orElseThrow(() -> new BusinessException(ErrorCode.AD_NOT_FOUND));

        List<String> storedNames = new ArrayList<>();
        List<AdFile> entities = new ArrayList<>();

        if (adContents != null) {
            for (MultipartFile f : adContents) {
                if (f == null || f.isEmpty()) continue;

                // 원본명/확장자 필요시 추출
                String title = adFileCreateRequest.getFileTitle();

                String stored = fileStorage.store(f); // 저장된 랜덤명
                storedNames.add(stored);

                AdFile adFile = AdFile.of(stored, title);
                // 필요 시 originalName/ext 필드 추가해 엔티티에 저장
                ad.addFile(adFile);
                entities.add(adFile);
            }
        }

        // 롤백 보상: 새 파일 삭제
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    storedNames.forEach(fileStorage::deleteQuietly);
                }
            }
        });
    }


    @Transactional
    public void updateAdFile(Long fileId, AdUpdateRequest req, List<MultipartFile> newFiles) {

        // 1) Ad 조회 (create와 동일하게 findById 사용)
        Ad ad = adRepository.findById(req.getAdId())
                .orElseThrow(() -> new BusinessException(ErrorCode.AD_NOT_FOUND));

        // 2) 기존 파일 엔티티 & 물리 파일명 확보 (커밋 후 삭제 대상)
        List<AdFile> oldEntities = adFileRepository.findByAd_AdId(req.getAdId());
        List<String> oldFileNames = oldEntities.stream()
                .map(AdFile::getFileTitle)
                .filter(Objects::nonNull)
                .toList();

        // 3) 새 파일 저장 (롤백 시 제거 대상 기록) + 전체 교체 전략
        List<String> savedNewFileNames = new ArrayList<>();
        if (newFiles != null && !newFiles.isEmpty()) {
            ad.clearFiles(); // orphanRemoval에 의해 기존 파일 엔티티 제거

            for (MultipartFile part : newFiles) {
                if (part == null || part.isEmpty()) continue;

                String stored = fileStorage.store(part); // 물리 저장
                savedNewFileNames.add(stored);

                // createAdFile과 동일하게 저장명/타이틀 사용
                String title = req.getTitle(); // 필요시 별도 타이틀 소스 사용
                AdFile adFile = AdFile.of(stored, title);
                ad.addFile(adFile); // 양방향 연결
            }
        }

        // 4) 광고의 나머지 필드 업데이트
        ad.updateAd(
                req.getTitle(),
                req.getLike_count(),
                req.getBookmark_count(),
                req.getComment_count(),
                req.getView_count()
        );

        // 5) 트랜잭션 종료 훅(커밋/롤백)에서 물리 파일 정리 (create와 동일한 패턴)
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            final List<String> finalsOld = List.copyOf(oldFileNames);
            final List<String> finalsNew = List.copyOf(savedNewFileNames);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_COMMITTED) {
                        // 커밋되면 기존 파일 삭제
                        for (String old : finalsOld) fileStorage.deleteQuietly(old);
                    } else {
                        // 롤백되면 새로 저장한 파일 삭제
                        for (String nv : finalsNew) fileStorage.deleteQuietly(nv);
                    }
                }
            });
        }
    }



    /* 광고 파일 삭제 */
    @Transactional
    public void deleteAdFile(Long AdFileId) {
        adFileRepository.deleteById(AdFileId);
    }

}