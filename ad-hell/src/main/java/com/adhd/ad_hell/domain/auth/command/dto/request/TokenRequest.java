package com.adhd.ad_hell.domain.auth.command.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenRequest {
    private String accessToken;
    private String refreshToken;
}
