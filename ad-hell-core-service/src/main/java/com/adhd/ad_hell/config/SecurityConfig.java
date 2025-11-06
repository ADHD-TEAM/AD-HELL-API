package com.adhd.ad_hell.config;

import com.adhd.ad_hell.common.ApiEndpoint;
import com.adhd.ad_hell.domain.auth.command.service.CustomUserDetailsService;
import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.jwt.JwtAuthentiationFilter;
import com.adhd.ad_hell.jwt.JwtTokenProvider;
import com.adhd.ad_hell.jwt.RestAccessDeniedHandler;
import com.adhd.ad_hell.jwt.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;    // 토큰 생성,검증 등
    private final CustomUserDetailsService userDetailsService;    // 로그인시 사용자 정보를 담는 곳
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint; //인증 실패 핸들러
    private final RestAccessDeniedHandler restAccessDeniedHandler;      // 인가 실패 핸들러


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            // 1. csrf 처리 : jwt 비활성화
        http.csrf(AbstractHttpConfigurer::disable)
            // 2. session 처리 ( jwt : STATELESS 설정)
            .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 3. method, url 기준 인증/인가 설정
            .authorizeHttpRequests(auth -> {

                /* Swagger 문서 공개 */
                auth.requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll();

                /* SSE 테스트 */

                // [A] SSE 테스트를 위한 예외 허용 (테스트용)
                // 정적 리소스 & 테스트 페이지
                auth.requestMatchers(
                        "/",              // 루트
                        "/index.html",
                        "/sse-test.html", // 우리가 만들 테스트 페이지
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico"
                ).permitAll();

                // SSE 구독 엔드포인트도 일단 모두 허용 (테스트용)
                auth.requestMatchers(
                        HttpMethod.GET,
                        "/api/users/*/notifications/stream"
                ).permitAll();

                /* SSE 테스트 */

//                // 테스트용: 문의 관련 API 전부 허용
//                auth.requestMatchers("/api/inquiries/**").permitAll();


                for(ApiEndpoint endpoint : ApiEndpoint.values()) {
                    if (endpoint.getRole() == null) {
                        // 회원가입, 로그인, 로그아웃
                        auth.requestMatchers(endpoint.getEndpointStatus(), endpoint.getPath()).permitAll();
                    } else {
                        if (endpoint.getRole() == Role.USER) {
                            // user : user/ admin
                            auth.requestMatchers(endpoint.getEndpointStatus(), endpoint.getPath())
                                    .hasAnyRole(Role.USER.name(), Role.ADMIN.name());
                        } else if (endpoint.getRole() == Role.ADMIN) {
                            // admin
                            auth.requestMatchers(endpoint.getEndpointStatus(), endpoint.getPath())
                                    .hasAnyRole(Role.ADMIN.name());
                                    //.permitAll();
                        }
                    }
                }
                auth.anyRequest().authenticated();
            })
                // 4. 인증 필터 추가 (JWT)
            .addFilterBefore(jwtAuthentiationFilter(), UsernamePasswordAuthenticationFilter.class)
            // 인증/인가 실패 처리
            .exceptionHandling(exception ->
                    // 인증 실패 핸들러
                    exception.authenticationEntryPoint(restAuthenticationEntryPoint)
                    // 인가 실패 핸들러
                            .accessDeniedHandler(restAccessDeniedHandler)
                    );
                // 프론트와 연결
        return http.build();
    }


    @Bean
    public JwtAuthentiationFilter jwtAuthentiationFilter() {
        return  new JwtAuthentiationFilter(jwtTokenProvider, userDetailsService);
    }
}
