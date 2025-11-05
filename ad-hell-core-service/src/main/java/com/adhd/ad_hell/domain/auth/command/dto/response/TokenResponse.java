package com.adhd.ad_hell.domain.auth.command.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}
