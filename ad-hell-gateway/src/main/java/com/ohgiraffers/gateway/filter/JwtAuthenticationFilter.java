package com.ohgiraffers.gateway.filter;

import com.ohgiraffers.gateway.jwt.GatewayJwtTokenProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final GatewayJwtTokenProvider gatewayJwtTokenProvider;
    private final WebClient.Builder webClientBuilder;

    public JwtAuthenticationFilter(GatewayJwtTokenProvider gatewayJwtTokenProvider, WebClient.Builder webClientBuilder) {
        this.gatewayJwtTokenProvider = gatewayJwtTokenProvider;
        this.webClientBuilder = webClientBuilder;
    }

//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        // 1. Authorization 헤더 추출
//        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
//
//        // 2. 퍼블릭 엔드포인트 허용 (있으면 검증, 없으면 통과)
//        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return chain.filter(exchange);
//        }
//
//        // 3. 토큰 문자열 추출
//        String token = authHeader.substring(7);
//
//        // 4. 토큰 검증 (서명 검증, 만료, 포맷 등)
//        try {
//            if(!gatewayJwtTokenProvider.validateToken(token)) {
//                // 유효하지 않은 토큰
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//        } catch (Exception e) {
//            // 파싱 오류 등 예외 발생도 401
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        // 5. 클레임에서 사용자 식별자와 역할 꺼내기
//        String publicId = gatewayJwtTokenProvider.getSubject(token);
//        String role = gatewayJwtTokenProvider.getRole(token);
//
//        // 6. user-service 로 UUID -> 내부 PK(Long) 조회
//        WebClient userClient = webClientBuilder.baseUrl("lb://SWCAMP-USER-SERVICE").build();
//
//        // 7. 내부 전용 API 호출
//        return userClient.get()
//                .uri(uriBuilder ->
//                        uriBuilder.path("/internal/users/resolve")
//                                .queryParam("publicId", publicId)
//                                .build())
//                // 내부 호출 보호용 시크릿 헤더
//                .header("X-Internal-Secret", "gateway-secret")
//                .retrieve()
//                .onStatus(
//                        HttpStatusCode::is4xxClientError,
//                        resp -> Mono.error(new RuntimeException("resolve 4XX")))
//                .onStatus(
//                        HttpStatusCode::is5xxServerError,
//                        resp -> Mono.error(new RuntimeException("resolve 5XX")))
//                // JSON body를 ResolveResponse로 매핑
//                .bodyToMono(ResolveResponse.class)
//                .flatMap(res -> {
//                    // 다운 스트림으로 넘길 요청 헤더 구성
//                    ServerHttpRequest mutated = exchange.getRequest().mutate()
//                            .header("X-User-PublicId", publicId)
//                            .header("X-User-Id", String.valueOf(res.userId))
//                            .header("X-User-Role", role)
//                            .build();
//
//                    return chain.filter(exchange.mutate().request(mutated).build());
//                })
//                .onErrorResume(e -> {
//                   // 실패 정책 기재
//                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                    return exchange.getResponse().setComplete();
//                });
//    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // TODO: 나중에 JWT 인증 다시 붙이기
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        // GlobalFilter의 실행 순서 지정
        // 값이 낮을 수록 먼저 실행한다. 일반적으로 인증, 로깅 등은 앞 단에서 실행 되기를 원함.
        return -1;
    }

    static record ResolveResponse(Long userId, String role) {}
}
