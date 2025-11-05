package com.adhd.ad_hell.domain.auth.command.controller;

import com.adhd.ad_hell.common.ApiEndpoint;
import com.adhd.ad_hell.common.dto.ApiResponse;
import com.adhd.ad_hell.domain.auth.command.dto.request.*;
import com.adhd.ad_hell.domain.auth.command.dto.response.ExistVerificationCodeResponse;
import com.adhd.ad_hell.domain.auth.command.dto.response.FindUserInfoResponse;
import com.adhd.ad_hell.domain.auth.command.dto.response.TokenResponse;
import com.adhd.ad_hell.domain.auth.command.service.AuthCommandService;
import com.adhd.ad_hell.domain.user.command.dto.request.UserSignUpRequest;
import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.command.service.UserCommandService;
import com.adhd.ad_hell.domain.user.command.service.UserCommandServiceImpl;
import com.adhd.ad_hell.exception.ErrorCode;
import com.adhd.ad_hell.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.token.TokenService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthCommandController {

    private final UserCommandService userCommandService;
    private final AuthCommandService authCommandService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     * @param userSignUpRequest
     * @return
     */
    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse<Void>> signUp(
            @Validated @RequestBody UserSignUpRequest userSignUpRequest
    ) {
        log.info("[AuthCommandController/signUp] 회원가입 | {}", userSignUpRequest);
        userCommandService.singUp(userSignUpRequest);

        log.info("[AuthCommandController/signUp] 회원가입 성공 | {}", userSignUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }

    /**
     * 로그인
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
           @Valid @RequestBody LoginRequest request
    ) {
        log.info("[AuthCommandController/login] 로그인 | {}", request);
        TokenResponse response = authCommandService.login(request);

        log.info("[AuthCommandController/login] 로그인 성공 | {}", request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 토큰 재발급
     * @param request
     * @return
     */
    @PostMapping("/token-reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> tokenReissue(
            @RequestBody TokenRequest request) {
        log.info("[AuthCommandController/tokenReissue] 리프레쉬 토큰 재발급");


        TokenResponse response = authCommandService.tokenReissue(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    /**
     * 로그아웃
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        log.info("[AuthCommandController/login] logout |");
        String accessToken = jwtTokenProvider.resolveToken(request);
        authCommandService.logout(accessToken);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 본인인증 - 이메일로 인증번호 보내기
     * @param request
     * @return
     */
    @PostMapping("/email/send-code")
    public ResponseEntity<ApiResponse<?>> sendVerificationCode(
            @RequestBody SendEmailVerifyUserRequest request) {
        log.info("[AuthCommandController/sendVerificationCode] 본인인증 - 이메일로 인증번호 보내기 |");
        authCommandService.sendVerificationCode(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 인증번호가 같은지 확인
     * @param request
     * @return
     */
    @PostMapping("/chek/verifi-code")
    public ResponseEntity<ApiResponse<ExistVerificationCodeResponse>> existVerificationCode(
            @RequestBody ExistVerificationCodeRequest request) {

        log.info("[AuthCommandController/existVerificationCode] 인증번호가 같은지 확인");
        ExistVerificationCodeResponse response = authCommandService.existVerificationCode(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 아이디/비밀번호 찾기
     * @param request
     * @return
     */
    @PostMapping("/chek/find-user")
    public ResponseEntity<ApiResponse<FindUserInfoResponse>> finduserInfo(
            @RequestBody ExistVerificationCodeRequest request) {
        log.info("[AuthCommandController/finduserInfo] 아이디/비밀번호 찾기");

        // 인증번호가 같은지 확인
        ExistVerificationCodeResponse existCode = authCommandService.existVerificationCode(request);
        if(Boolean.FALSE.equals(existCode.getExist())) {
            log.info("[AuthCommandController/finduserInfo] 아이디/비밀번호 찾기 - 인증번호 다름 ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(ErrorCode.VERIFI_CODE_DIFFERENT.getCode()
                            , ErrorCode.VERIFI_CODE_DIFFERENT.getMessage()));
        }

        log.info("[AuthCommandController/finduserInfo] 인증번호 {} 아이디/비밀번호 찾기  ",  existCode.getExist());
        FindUserInfoResponse response = authCommandService.getUserInfo(request.getEmail(), request.getLoginId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 비밀번호 재설정
     * @param request
     * @return
     */
    @PatchMapping("/chek/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        log.info("[AuthCommandController/resetPassword] 비밀번호 재설정 ");

        authCommandService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }



}
