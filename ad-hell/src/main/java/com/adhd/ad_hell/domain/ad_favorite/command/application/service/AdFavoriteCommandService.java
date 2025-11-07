package com.adhd.ad_hell.domain.ad_favorite.command.application.service;

import com.adhd.ad_hell.domain.ad_favorite.command.application.dto.request.AdFavoriteCreateRequest;
import com.adhd.ad_hell.domain.ad_favorite.command.application.dto.response.AdFavoriteCommandResponse;
import com.adhd.ad_hell.domain.ad_favorite.command.domain.aggregate.AdFavorite;
import com.adhd.ad_hell.domain.ad_favorite.command.domain.repository.AdFavoriteRepository;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdFavoriteCommandService {

    private final AdFavoriteRepository adFavoriteRepository;
    private final AdRepository adRepository;
    private final UserCommandRepository userRepository;

    /** 즐겨찾기 등록 */
    public AdFavoriteCommandResponse AdFavoriteCreate(AdFavoriteCreateRequest req) {

        Ad ad = adRepository.findById(req.getAdId())
                .orElseThrow(() -> new BusinessException(ErrorCode.AD_NOT_FOUND));

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 중복 등록 방지
        if (adFavoriteRepository.exists(user.getUserId(), ad.getAdId())) {
            throw new BusinessException(ErrorCode.AD_FAVORITE_ALREADY_EXISTS);
        }

        // 즐겨찾기 등록
        AdFavorite saved = adFavoriteRepository.save(AdFavorite.create(ad, user));

        return AdFavoriteCommandResponse.builder()
                .favId(saved.getId())
                .userId(user.getUserId())
                .adId(ad.getAdId())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    /** 즐겨찾기 삭제 */
    public void AdFavoriteDelete(Long userId, Long adId) {
        if (!adFavoriteRepository.exists(userId, adId)) {
            throw new BusinessException(ErrorCode.AD_FAVORITE_NOT_FOUND);
        }

        adFavoriteRepository.delete(userId, adId);
    }
}
