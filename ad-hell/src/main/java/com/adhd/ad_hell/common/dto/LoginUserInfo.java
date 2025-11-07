package com.adhd.ad_hell.common.dto;

import com.adhd.ad_hell.domain.user.command.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.annotation.processing.Generated;

@Getter
@Builder
@AllArgsConstructor
public class LoginUserInfo {

    private Long userId;
    private String loginId;
    private Role role;
}
