package com.adhd.ad_hell.domain.user.command.service;

import com.adhd.ad_hell.domain.user.command.dto.request.AdminModifyRequest;
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
public class AdminCommandServiceImpl implements AdminCommandService {

    private final UserCommandRepository userCommandRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void modifyByUserInfo(AdminModifyRequest adminModifyRequest, Long userId) {
        log.debug("[AdminCommandServiceImpl/modifyByUserInfo] 관리자-회원정보수정");


        User findUser = userCommandRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        findUser.modify(adminModifyRequest, passwordEncoder);
        userCommandRepository.save(findUser);

    }

    @Override
    @Transactional
    public void patchByUserStatus(Long userId, UserStatus status) {
        log.debug("[AdminCommandServiceImpl/patchByUserStatus] 관리자-상태변경 ");
        User findUser = userCommandRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        findUser.patchStatus(status);
        userCommandRepository.save(findUser);

    }
}
