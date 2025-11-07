package com.adhd.ad_hell.domain.user.command.controller;


import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.common.dto.CustomUserDetails;
import com.adhd.ad_hell.domain.user.command.dto.request.UserIsAvailableRequest;
import com.adhd.ad_hell.domain.user.command.dto.request.UserModifyRequest;
import com.adhd.ad_hell.domain.user.command.dto.response.UserDetailResponse;
import com.adhd.ad_hell.domain.user.command.dto.response.UserIsAvailableResponse;
import com.adhd.ad_hell.domain.user.command.service.UserCommandService;
import com.adhd.ad_hell.domain.user.command.service.UserCommandServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Command", description = "사용자 API")
public class UserCommandController {

    private final UserCommandService userCommandService;


    /**
     * 사용할 수 있는 닉네임 확인
     * @param userIsAvailableRequest
     * @return
     */
    @Operation(
            summary = "사용할 수 있는 닉네임 확인",
            description = "회원가입시, 닉네임 변경시 사용할 수 있는 닉네임인지 확인할 수 있음"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
    })
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
    @Operation(
            summary = "마이페이지",
            description = "사용자는 마이페이지를 조회할 수 있다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
    })
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
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdrawByUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("[UserCommandController/withdrawByUserInfo] 사용자 - 회원 탈퇴");

        userCommandService.withdrawByUserInfo(userDetails);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
