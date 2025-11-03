package com.adhd.ad_hell.domain.user.query.dto;

import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.command.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserDTO {
    private Long userId;
    private String roleType;
    private String loginId;
    private String nickname;
    private String email;
    private String status;
    private Long amount;
    private LocalDateTime deactivatedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
