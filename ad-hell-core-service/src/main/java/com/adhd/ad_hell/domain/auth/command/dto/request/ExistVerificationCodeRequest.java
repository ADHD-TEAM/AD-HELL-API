package com.adhd.ad_hell.domain.auth.command.dto.request;

import lombok.Getter;

@Getter
public class ExistVerificationCodeRequest {
    private String verificationCode;
    private String email;
    private String loginId;
}
