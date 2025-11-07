package com.adhd.ad_hell.config;

import com.adhd.ad_hell.security.NotificationJwtAuthenticationFilter;
import com.adhd.ad_hell.security.NotificationJwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final NotificationJwtTokenProvider tokenProvider;

    @Bean
    public SecurityFilterChain notificationSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Swagger / 문서
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 정적 리소스, sse-test.html
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/sse-test.html",
                                "/static/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()

                        // 🔓 개발용: SSE 스트림 엔드포인트는 토큰 없이 허용
                        .requestMatchers("/api/users/*/notifications/stream").permitAll()

                        // 🔐 그 외 사용자 알림 관련 API 는 인증 필수
                        .requestMatchers("/api/users/*/notifications/**").authenticated()

                        // 내부 호출은 열어둠
                        .requestMatchers("/internal/notifications/**").permitAll()

                        // 관리자용 API
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 나머지는 전부 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        notificationJwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public NotificationJwtAuthenticationFilter notificationJwtAuthenticationFilter() {
        return new NotificationJwtAuthenticationFilter(tokenProvider);
    }
}
