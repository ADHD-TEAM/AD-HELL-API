package com.adhd.ad_hell.domain.user.query.service;


import com.adhd.ad_hell.domain.user.query.dto.UserDTO;
import com.adhd.ad_hell.domain.user.query.dto.request.AdminSearchRequest;

import java.util.List;

public interface AdminQueryService {
    List<UserDTO> findAllByUsers(AdminSearchRequest adminSearchRequest);

    UserDTO findByUserId(Long userId);
}
