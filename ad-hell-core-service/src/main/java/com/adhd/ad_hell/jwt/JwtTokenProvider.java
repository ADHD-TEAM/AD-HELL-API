package com.adhd.ad_hell.jwt;

import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.command.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties; // JWT 관련 DTO class
    private SecretKey secretKey;

    @PostConstruct
    public void init() {    // 시크릿 키 초기화
        secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    /**
     * access token 생성
     * @param loginId
     * @param role
     * @return
     */
    public String createAccessToken(String loginId, Role role) {
        Date now = new Date();

        // 토큰 발급 시점 (현재 시간 기준)
        Date expriyDate = new Date(now.getTime() + jwtProperties.getExpiration());
        return Jwts.builder()
                .subject(loginId)
                .claim("role", Role.valueOf(role.name()))
                .issuedAt(now)
                .expiration(expriyDate)
                .signWith(secretKey,Jwts.SIG.HS512 )
                .compact();
    }


    /**
     * refresh token 생성
     * @param loginId
     * @param role
     * @return
     */
    public String createRefreshToken(String loginId, Role role) {
        Date now = new Date();
        // 토큰 발급 시점 (현재 시간 기준)
        Date expriyDate = new Date(now.getTime() + jwtProperties.getRefreshExpiration());
        return Jwts.builder()
                .subject(loginId)
                .claim("role", Role.valueOf(role.name()))
                .issuedAt(now)
                .expiration(expriyDate)
                .signWith(secretKey,Jwts.SIG.HS512 )
                .compact();
    }


    /**
     * 토큰 검증
     * @param token
     * @return
     */
    public boolean vaildateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new BadCredentialsException("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            throw new BadCredentialsException("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            throw new BadCredentialsException("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("JWT Token claims empty", e);
        }
    }

    /**
     * 토큰으로 userId 가져오기
     * @param token
     * @return
     */
    public String getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * 토큰으로 토큰 남은 유효시간 가져오기
     * @param accessToken
     * @return
     */
    public long getRemainingTime(String accessToken) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey) //문자열 key 반환
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        Date expiration = claims.getExpiration();
        long now = System.currentTimeMillis();
        return expiration.getTime() - now;
    }

    /**
     * 헤더에서 access token 가져오기
     * @param request
     * @return
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    // 알림 검증용 메소드들
    public String createAccessTokenWithUser(String loginId, Long userId, Role role) {
        Date now = new Date();
        Date expriyDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .subject(loginId)              // 그대로 loginId
                .claim("userId", userId)       // 🟢 새로 추가
                .claim("role", role.name())    // 문자열로
                .issuedAt(now)
                .expiration(expriyDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public String createRefreshTokenWithUser(String loginId, Long userId, Role role) {
        Date now = new Date();
        Date expriyDate = new Date(now.getTime() + jwtProperties.getRefreshExpiration());

        return Jwts.builder()
                .subject(loginId)
                .claim("userId", userId)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expriyDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

}
