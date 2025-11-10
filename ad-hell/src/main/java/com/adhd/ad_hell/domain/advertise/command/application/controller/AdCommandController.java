package com.adhd.ad_hell.domain.advertise.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdCreateRequest;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdUpdateRequest;
import com.adhd.ad_hell.domain.advertise.command.application.service.AdCommandService;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdLike;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

// ... existing code ...

import java.util.Map;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ads")
@Tag(name = "Ad Command", description = "광고 관리 API")
public class AdCommandController {

    private final AdCommandService adCommandService;
    private final AdRepository adRepository;
    private final SecurityUtil securityUtil;

    @Operation(summary = "광고 등록", description = "광고를 등록하고 영상 파일을 함께 업로드한다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createAd(
        @RequestPart("adInfo") AdCreateRequest req,
        @RequestPart(value = "videoFiles", required = false) List<MultipartFile> videoFiles
    ) {
        Long adId = adCommandService.createAd(req, videoFiles);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(Map.of("adId", adId));
    }

    @Operation(summary = "광고 수정", description = "광고의 제목, 카테고리 등 정보를 수정한다.")
    @PutMapping("/{adId}")
    public ResponseEntity<ApiResponse<Void>> updateAd(
        @PathVariable Long adId,
        @RequestBody AdUpdateRequest req
    ) {
        adCommandService.updateAd(adId, req);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "광고 삭제", description = "광고와 관련 파일을 모두 삭제한다.")
    @DeleteMapping("/{adId}")
    public ResponseEntity<Map<String, Object>> deleteAd(@PathVariable Long adId) {
        adCommandService.deleteAd(adId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{adId}/likes/toggle")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long adId) {

        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AD_NOT_FOUND));
        Long userId = securityUtil.getLoginUserInfo().getUserId();

        adCommandService.toggleLike(ad, userId);
        return ResponseEntity.ok().build();
    }

}
