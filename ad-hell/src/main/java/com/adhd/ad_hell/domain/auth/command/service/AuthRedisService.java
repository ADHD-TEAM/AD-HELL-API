package com.adhd.ad_hell.domain.auth.command.service;

public interface AuthRedisService {
    void saveValidityCode(String email, String code);
    Boolean existVerificationCode(String email, String Code);
    void deleteKeyEmail(String email);
    void saveRefreshToken(Long userId, String refreshToken);
    Boolean existRefreshTokenByUserId(Long userId);
    void deleteRefreshTokenByUserId(Long userId);
    void addBlackListAccessToken(String accessToken, long remainingTime);

    String findKeyByUserId(Long userId);
}
