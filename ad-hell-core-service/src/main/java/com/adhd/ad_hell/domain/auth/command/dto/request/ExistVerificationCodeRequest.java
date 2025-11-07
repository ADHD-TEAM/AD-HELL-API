package com.adhd.ad_hell.domain.auth.command.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExistVerificationCodeRequest {
    private String verificationCode;
    private String email;
    private String loginId;
}
