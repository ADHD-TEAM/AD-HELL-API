package com.adhd.ad_hell.domain.user.command.service;

import com.adhd.ad_hell.common.dto.CustomUserDetails;
import com.adhd.ad_hell.domain.user.command.dto.request.UserIsAvailableRequest;
import com.adhd.ad_hell.domain.user.command.dto.request.UserModifyRequest;
import com.adhd.ad_hell.domain.user.command.dto.request.UserSignUpRequest;
import com.adhd.ad_hell.domain.user.command.dto.response.UserDetailResponse;
import com.adhd.ad_hell.domain.user.command.dto.response.UserIsAvailableResponse;
import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.entity.UserStatus;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserCommandRepository userCommandRepository;
    private final PasswordEncoder passwordEncoder;
    /**
     * 사용할 수 있는 닉네임인지 확인
     * @param userIsAvailableRequest
     * @return
     */
    public UserIsAvailableResponse isAvailable(UserIsAvailableRequest userIsAvailableRequest) {
        log.debug("[UserCommandController/isAvailable] 사용할 수 있는 닉네임 확인 | {}", userIsAvailableRequest);
       Boolean response = userCommandRepository.existsByNickname(userIsAvailableRequest.getNickname());
       return UserIsAvailableResponse.toUserIsAvailableResponse(response);
    }

    /**
     * 회원가입
     *
     * @param userSignUpRequest
     * @return
     */
    @Override
    @Transactional
    public void singUp(UserSignUpRequest userSignUpRequest) {
        log.debug("[UserCommandController/singUp] 회원가입 | {}", userSignUpRequest);

        log.debug("[UserCommandController/singUp] 사용할 수 있는 아이디인지 확인 | {}", userSignUpRequest);
        Boolean exist = userCommandRepository.existsByloginId(userSignUpRequest.getLoginId());
        if (exist) {
            log.error("[UserCommandController/singUp] 사용할 수 없음");
            throw new BusinessException(ErrorCode.LOGIN_ID_ALREADY_EXISTS);
        }

        // 사용할 수 있는 닉네임인지 확인
        log.debug("[UserCommandController/singUp] 사용할 수 있는 닉네임인지 확인 | {}", userSignUpRequest);
        exist = userCommandRepository.existsByNickname(userSignUpRequest.getNickname());
        if (exist) {
            log.error("[UserCommandController/singUp] 사용할 수 없음");
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        // 회원가입
        log.debug("[UserCommandController/singUp] 회원가입 | {}");
        User signUpUser = User.builder()
                .roleType(Role.USER)
                .loginId(userSignUpRequest.getLoginId())
                .password(passwordEncoder.encode(userSignUpRequest.getPassword()))
                .nickname(userSignUpRequest.getNickname())
                .email(userSignUpRequest.getEmail())
                .build();

        userCommandRepository.save(signUpUser);
    }

    /**
     * 마이페이지 회원정보 조회
     * @param userDetails
     * @return
     */
    @Override
    @Transactional
    public UserDetailResponse getUserDetail(CustomUserDetails userDetails) {
        log.debug("[UserCommandController/getUserDetail] 마이페이지 회원정보 조회 |");
        User findUser = userCommandRepository.findByLoginId(userDetails.getLoginId())
                .orElseThrow(() ->new BusinessException(ErrorCode.USER_NOT_FOUND));

        log.debug("[UserCommandController/getUserDetail] 마이페이지 회원정보 조회");
        return UserDetailResponse.builder()
                .loginId(findUser.getLoginId())
                .nickname(findUser.getNickname())
                .email(findUser.getEmail())
                .status(findUser.getStatus())
                .createdAt(findUser.getCreatedAt())
                .deactivatedAt(findUser.getDeactivatedAt())
                .amount(findUser.getAmount())
                .build();
    }

    @Override
    @Transactional
    public void modifyByUserInfo(CustomUserDetails userDetails , UserModifyRequest userModifyRequest) {
        log.debug("[UserCommandController/modifyByUserInfo] 회원정보 수정 |");

        User findUser = userCommandRepository.findByLoginId(userDetails.getLoginId())
                .orElseThrow(() ->new BusinessException(ErrorCode.USER_NOT_FOUND));

        findUser.modifyByUserInfo(userModifyRequest);
        userCommandRepository.save(findUser);

    }

    @Override
    @Transactional
    public void withdrawByUserInfo(CustomUserDetails userDetails) {
        log.debug("[UserCommandController/withdrawByUserInfo] 회원탈퇴 |");

        User findUser = userCommandRepository.findByLoginId(userDetails.getLoginId())
                .orElseThrow(() ->new BusinessException(ErrorCode.USER_NOT_FOUND));

        findUser.patchStatus(UserStatus.DELETE);
        userCommandRepository.save(findUser);

    }

}
