package com.adhd.ad_hell.domain.auth.command.service;

import com.adhd.ad_hell.common.dto.CustomUserDetails;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserCommandRepository userCommandRepository;

    @Override
    @Transactional
    public CustomUserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        log.debug("[CustomUserDetailsService/loadUserByUsername] loadUserByUsername");
        User user = userCommandRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return new CustomUserDetails(
                user.getUserId()
                , user.getLoginId()
                , user.getPassword()
                , user.getRoleType()
        );
    }
}
