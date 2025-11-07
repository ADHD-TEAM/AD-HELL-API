package com.adhd.ad_hell.domain.auth.command.service;

import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthRedisServiceImpl implements AuthRedisService {

    private final long EMAIL_VALIDITY_SECONDS = 5; // 인증번호 기본 시간
    private final long REFRESH_TOKEN_EXPIRY = 14; // 2주

    private final RedisTemplate<String, Object> redisTemplate;

    // 인증번호 저장
    @Override
    public void saveValidityCode(String email, String code) {
        String key = "email:verify:" + email;

        // redis 저장
        redisTemplate.opsForValue()
                .set(key, code,EMAIL_VALIDITY_SECONDS, TimeUnit.MINUTES);

        log.info("[AuthRedisServiceImpl/saveValidityCode] 인증번호 저장 성공 | ");
    }

    // 인증번호 확인
    @Override
    public Boolean existVerificationCode(String email, String code) {
        String key = "email:verify:" + email;

        if (!redisTemplate.hasKey(key)) {
            // key가 없는 경우
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        String originCode = redisTemplate.opsForValue().get(key).toString();
        log.info("[AuthRedisServiceImpl/existVerificationCode] 인증번호 확인  email={} ", email);

        // 레디스에 있는 인증코드와 받은 코드가 같은지 확인
        return Objects.equals(originCode, code);
    }

    // key로 value 삭제하기
    @Override
    public void deleteKeyEmail(String email) {
        log.info("[AuthRedisServiceImpl/deleteKeyEmail] key로 value 삭제하기  email={} ", email);
        String key = "email:verify:" + email;

        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
            log.info("[AuthRedisServiceImpl/deleteKeyEmail] key로 value 삭제하기 email={}", email);
        } else {
            log.info("[AuthRedisServiceImpl/deleteKeyEmail] key를 찾을 수 없음 email={}", email);
        }
    }

    @Override
    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = "refreshToken:" + userId;

        // redis 저장
        redisTemplate.opsForValue().set(
                key,refreshToken,REFRESH_TOKEN_EXPIRY, TimeUnit.DAYS);

        log.info("[AuthRedisServiceImpl/saveRefreshToken] userId로 refreshToken 저장");

    }

    @Override
    public Boolean existRefreshTokenByUserId(Long userId) {
        log.info("[AuthRedisServiceImpl/existRefreshTokenByUserId] userId로 refresh token 확인");
        String key = "refreshToken:" + userId;

        return redisTemplate.hasKey(key);
    }

    @Override
    public void deleteRefreshTokenByUserId(Long userId) {
        log.info("[AuthRedisServiceImpl/deleteRefreshTokenByUserId] userId로 refresh token 확인");
        String key = "refreshToken:" + userId;

        if (redisTemplate.hasKey(key)) {
            redisTemplate.delete(key);
            log.info("[AuthRedisServiceImpl/deleteRefreshTokenByUserId] 리프레쉬 토큰 삭제 userId={}", userId);
        } else {
            log.info("[AuthRedisServiceImpl/deleteRefreshTokenByUserId] 리프레쉬 토큰 찾을 수 없음 userId={}", userId);
        }

    }

    @Override
    public void addBlackListAccessToken(String accessToken, long remainingTime) {
        log.info("[AuthRedisServiceImpl/addBlackListAccessToken] access token 블랙리스트 추가 remainingTime = {}" , accessToken);
        String key = "blackList:" + accessToken;

        redisTemplate.opsForValue().set(key, "logout", remainingTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public String findKeyByUserId(Long userId) {
        log.info("[AuthRedisServiceImpl/findKeyByUserId] userId로 refreshToken 찾기");
        String key = "refreshToken:" + userId;

        return redisTemplate.opsForValue().get(key).toString();
    }
}
