package com.adhd.ad_hell.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class NotificationJwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;   // 코어와 동일한 값

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        //  코어의 JwtTokenProvider 와 동일한 방식으로 키 생성
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Authorization 헤더에서 Bearer 토큰 추출 */
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    /** 토큰 유효성 검증 */
    public boolean validateToken(String token) {
        try {
            getClaims(token);  // 여기서 서명 / 만료 다 체크
            return true;
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /** subject = loginId */
    public String getLoginId(String token) {
        return getClaims(token).getSubject();
    }

    /** userId 클레임 꺼내기 (코어에서 넣었다는 가정) */
    public Long getUserId(String token) {
        Object claim = getClaims(token).get("userId");
        if (claim instanceof Number num) {
            return num.longValue();
        }
        if (claim instanceof String s) {
            return Long.parseLong(s);
        }
        return null;
    }

    /** role 클레임 꺼내기 */
    public String getRole(String token) {
        Object claim = getClaims(token).get("role");
        return claim != null ? claim.toString() : null;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)     // 코어와 같은 키로 서명 검증
                .build()
                .parseSignedClaims(token)  // parseSignedClaims 사용
                .getPayload();             // claims
    }
}
