package com.adhd.ad_hell.domain.user.command.service;

import com.adhd.ad_hell.domain.user.command.dto.request.AdminModifyRequest;
import com.adhd.ad_hell.domain.user.command.entity.UserStatus;

public interface AdminCommandService {
    void modifyByUserInfo(AdminModifyRequest adminModifyRequest, Long userId);

    void patchByUserStatus(Long userId, UserStatus status);
}
