package com.adhd.ad_hell.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain notificationSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                // JWT 쓸 준비는 해두되, 당장은 단순 모드
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Swagger / 문서
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 알림 관련 API들 전부 허용 (테스트용)
                        .requestMatchers(
                                "/api/notifications/**",
                                "/api/users/*/notifications/**",
                                "/api/admin/notifications/**",
                                "/internal/notifications/**"
                        ).permitAll()

                        // 그 외 나머지도 일단 전부 허용
                        .anyRequest().permitAll()
                );

        // ❗ jwtAuthentiationFilter, CustomUserDetailsService, ApiEndpoint 등은 여기서 전혀 사용하지 않음!
        return http.build();
    }
}
