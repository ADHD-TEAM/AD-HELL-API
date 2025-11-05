package com.adhd.ad_hell.domain.user.query.dto.request;

import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.command.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class AdminSearchRequest {

    private String loginId;
    private String nickname;
    private String email;
    private Role roleType;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime deactivatedAt;
    private LocalDateTime deletedAt;




}
