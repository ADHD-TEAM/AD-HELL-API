package com.adhd.ad_hell.domain.user.command.service;

import com.adhd.ad_hell.common.dto.CustomUserDetails;
import com.adhd.ad_hell.domain.user.command.dto.request.UserIsAvailableRequest;
import com.adhd.ad_hell.domain.user.command.dto.request.UserModifyRequest;
import com.adhd.ad_hell.domain.user.command.dto.request.UserSignUpRequest;
import com.adhd.ad_hell.domain.user.command.dto.response.UserDetailResponse;
import com.adhd.ad_hell.domain.user.command.dto.response.UserIsAvailableResponse;
import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.command.entity.User;

public interface UserCommandService {

    // 사용여부
    UserIsAvailableResponse isAvailable(UserIsAvailableRequest userIsAvailableRequest);
    // 회원가입
    void singUp(UserSignUpRequest userSignUpRequest);
    // 마이페이지
    UserDetailResponse getUserDetail(CustomUserDetails userDetails);
    // 회원정보 수정
    void modifyByUserInfo(CustomUserDetails userDetails, UserModifyRequest userModifyRequest);

    void withdrawByUserInfo(CustomUserDetails userDetails);
}
