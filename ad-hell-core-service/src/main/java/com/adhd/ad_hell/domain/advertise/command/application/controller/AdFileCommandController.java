package com.adhd.ad_hell.domain.advertise.command.application.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdCreateRequest;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdFileCreateRequest;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdUpdateRequest;
import com.adhd.ad_hell.domain.advertise.command.application.dto.response.AdFileCommandResponse;
import com.adhd.ad_hell.domain.advertise.command.application.service.AdFileCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/adFiles")
public class AdFileCommandController {

    private final AdFileCommandService AdFileCommandService;

    // 광고 생성: JSON + 파일(multipart/form-data)
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<AdFileCommandResponse>> createAdFile(
            @RequestPart AdFileCreateRequest adFileCreateRequest,
            @RequestPart List<MultipartFile> adContents
    ) {
        AdFileCommandService.createAdFile(adFileCreateRequest, adContents);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }

    // 광고 수정: 파일 전체 교체 전략(JSON + 다중 파일)
    @PutMapping(value = "/{fileId}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Void>> updateAdFile(
            @PathVariable Long fileId,
            @RequestPart AdUpdateRequest adUpdateRequest,
            @RequestPart(required = false) List<MultipartFile> newFiles
    ) {
        AdFileCommandService.updateAdFile(fileId, adUpdateRequest, newFiles);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 광고 파일 삭제(soft delete 또는 설정에 따름)
    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse<Void>> deleteAdFile(
            @PathVariable Long fileId
    ) {
        AdFileCommandService.deleteAdFile(fileId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
