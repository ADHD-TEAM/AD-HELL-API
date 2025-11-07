package com.adhd.ad_hell.domain.auth.command.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendEmailVerifyUserRequest {
    private String loginid;
    private String email;
}
