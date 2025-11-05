package com.adhd.ad_hell.domain.auth.command.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExistVerificationCodeResponse {
    private Boolean exist;
}
