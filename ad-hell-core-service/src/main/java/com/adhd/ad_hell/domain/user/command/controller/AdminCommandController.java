package com.adhd.ad_hell.domain.user.command.controller;

import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.user.command.dto.request.AdminModifyRequest;
import com.adhd.ad_hell.domain.user.command.entity.UserStatus;
import com.adhd.ad_hell.domain.user.command.service.AdminCommandService;
import com.adhd.ad_hell.domain.user.query.service.AdminQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admins")
public class AdminCommandController {

    private final AdminCommandService adminCommandService;

    /**
     * 관리자 - 회원정보 수정
     * @param userId
     * @param adminModifyRequest
     * @return
     */
    @PutMapping("/modify/{userId}")
    public ResponseEntity<ApiResponse<Void>> modifyByUserInfo(
            @PathVariable("userId") Long userId
            , @RequestBody AdminModifyRequest adminModifyRequest) {

        adminCommandService.modifyByUserInfo(adminModifyRequest, userId);
        return ResponseEntity.ok(ApiResponse.success(null));

    }

    /**
     * 관리자 - 회원 상태 변경
     * @param userId
     * @param status
     * @return
     */
    @PatchMapping("/patch/{userId}/{status}")
    public ResponseEntity<ApiResponse<Void>> patchByUserInfo(
            @PathVariable("userId") Long userId
            , @PathVariable("status") UserStatus status) {

        adminCommandService.patchByUserStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
