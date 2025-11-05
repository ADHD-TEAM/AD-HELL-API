package com.adhd.ad_hell.domain.user.command.controller;


import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.common.dto.CustomUserDetails;
import com.adhd.ad_hell.domain.user.command.dto.request.UserIsAvailableRequest;
import com.adhd.ad_hell.domain.user.command.dto.request.UserModifyRequest;
import com.adhd.ad_hell.domain.user.command.dto.response.UserDetailResponse;
import com.adhd.ad_hell.domain.user.command.dto.response.UserIsAvailableResponse;
import com.adhd.ad_hell.domain.user.command.service.UserCommandService;
import com.adhd.ad_hell.domain.user.command.service.UserCommandServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserCommandController {

    private final UserCommandService userCommandService;


    /**
     * 사용할 수 있는 닉네임 확인
     * @param userIsAvailableRequest
     * @return
     */
    @GetMapping("/isAvailable")
    public ResponseEntity<ApiResponse<UserIsAvailableResponse>> isAvailable(
            @RequestParam UserIsAvailableRequest userIsAvailableRequest) {
        log.info("[UserCommandController/isAvailable] 사용할 수 있는 닉네임 확인 | {}", userIsAvailableRequest);
        UserIsAvailableResponse response = userCommandService.isAvailable(userIsAvailableRequest);
        // userIsAvailable-false : 사용해도 됨
        // userIsAvailable-true : 사용할 수 없음
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    /**
     *  마이페이지
     * @return
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("[UserCommandController/getUserDetail] 마이페이지 ");
        // Username >> userId
        UserDetailResponse response = userCommandService.getUserDetail(userDetails);
        log.info("[UserCommandController/getUserDetail] 마이페이지 성공 ");
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    /**
     * 사용자 - 정보 수정
     * @param userDetails
     * @param userModifyRequest
     * @return
     */
    @PutMapping("/modify/info")
    public ResponseEntity<ApiResponse<Void>>  modifyByUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
            , @RequestBody UserModifyRequest userModifyRequest
    ) {
        log.info("[UserCommandController/modifyInfo] 사용자 - 정보 수정");

        userCommandService.modifyByUserInfo(userDetails, userModifyRequest);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 회원 탈퇴
     * @param userDetails
     * @return
     */
    @PatchMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdrawByUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("[UserCommandController/withdrawByUserInfo] 사용자 - 회원 탈퇴");

        userCommandService.withdrawByUserInfo(userDetails);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
