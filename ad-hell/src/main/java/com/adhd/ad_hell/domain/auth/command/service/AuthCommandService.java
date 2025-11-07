package com.adhd.ad_hell.domain.auth.command.service;


import com.adhd.ad_hell.domain.auth.command.dto.request.*;
import com.adhd.ad_hell.domain.auth.command.dto.response.ExistVerificationCodeResponse;
import com.adhd.ad_hell.domain.auth.command.dto.response.FindUserInfoResponse;
import com.adhd.ad_hell.domain.auth.command.dto.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthCommandService {
    TokenResponse login(LoginRequest request);
    void logout(String request);
    void sendVerificationCode(SendEmailVerifyUserRequest request);
    ExistVerificationCodeResponse existVerificationCode(ExistVerificationCodeRequest request);
    FindUserInfoResponse getUserInfo(String email, String loginid);
    void resetPassword(ResetPasswordRequest password);
    TokenResponse tokenReissue(TokenRequest request);
}
