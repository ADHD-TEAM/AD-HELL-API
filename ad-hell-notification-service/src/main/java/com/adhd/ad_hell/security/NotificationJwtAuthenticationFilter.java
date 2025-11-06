package com.adhd.ad_hell.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class NotificationJwtAuthenticationFilter extends OncePerRequestFilter {

    private final NotificationJwtTokenProvider tokenProvider;   // ✅ 패키지/타입 통일

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = tokenProvider.resolveToken(request);

        if (token != null && tokenProvider.validateToken(token)) {
            try {
                Long userId = tokenProvider.getUserId(token);
                String loginId = tokenProvider.getLoginId(token);
                String role = tokenProvider.getRole(token);

                if (userId != null && loginId != null) {
                    NotificationUserPrincipal principal =
                            new NotificationUserPrincipal(userId, loginId, role);

                    var auth = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            principal.getAuthorities()
                    );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                log.warn("JWT 파싱/설정 중 오류 발생: {}", e.getMessage());
                // 여기서는 그냥 인증 없이 흐름만 이어감
            }
        }

        filterChain.doFilter(request, response);
    }
}
