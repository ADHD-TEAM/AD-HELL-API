package com.adhd.ad_hell.domain.user.command.dto.request;

import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.command.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminModifyRequest {
    private String nickname;
    private String email;
    private String password;
    private UserStatus status;
    private Role role;
}
