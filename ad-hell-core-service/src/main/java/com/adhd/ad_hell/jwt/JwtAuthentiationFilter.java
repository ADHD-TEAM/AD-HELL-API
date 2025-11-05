package com.adhd.ad_hell.jwt;


import com.adhd.ad_hell.common.dto.CustomUserDetails;
import com.adhd.ad_hell.domain.auth.command.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
public class JwtAuthentiationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    // private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("[JwtAuthentiationFilter/doFilterInternal] 검증" );
        // 요청 헤더에서 JWT 토큰 추출
        String token = getJwtFromRequest(request);
        log.debug("[JwtAuthentiationFilter/doFilterInternal] 요청 헤더에서 JWT 토큰 추출 | {}" , token );
        // 토큰이 있을 경우
        if (StringUtils.hasText(token) && jwtTokenProvider.vaildateToken(token)) {
            log.debug("[JwtAuthentiationFilter/doFilterInternal] 토큰이 있을 경우 | " );
            // 유저 정보 추출
            String loginId = jwtTokenProvider.getUserIdFromJWT(token);
            log.debug("[JwtAuthentiationFilter/doFilterInternal] 유저 정보 추출 |{} ", loginId );
            // DB에서 사용자 정보 조회
            CustomUserDetails userDetails = userDetailsService.loadUserByUsername(loginId);
           // UserDetails userDetails = userDetailsService.loadUserByUsername(loginId);

            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            // SecurityContextHoler 인증 정보 저장
            log.debug("[JwtAuthentiationFilter/doFilterInternal] 인증 객체 생성 |{} ", authenticationToken );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.debug("[JwtAuthentiationFilter/doFilterInternal] 인증 객체 저장  |{} " );
        }

        filterChain.doFilter(request,response);
    }

    /**
     * 헤더에서 JWT 토큰 추출
     * @param request
     * @return
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        log.debug("[JwtAuthentiationFilter/getJwtFromRequest] 헤더에서 JWT 토큰 추출" );
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();

        }
        return null;
    }
}
