package com.adhd.ad_hell.common.util;

import com.adhd.ad_hell.common.dto.CustomUserDetails;
import com.adhd.ad_hell.common.dto.LoginUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    public static LoginUserInfo getLoginUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication.getPrincipal() == null) {
            // 로그인 실패
            log.info("[SecurityUtil/getLoginUserInfo] ########### 로그인 실패 ##################");
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            // 로그인 성공
            log.info("[SecurityUtil/getLoginUserInfo] ########### 로그인 성공 ##################");
            log.info("[SecurityUtil/getLoginUserInfo] userDetails.getUserId()= {},userDetails.getUsername()={}, userDetails.getRole()={} ",userDetails.getUserId(),userDetails.getUsername(),userDetails.getRole());
            return new LoginUserInfo(userDetails.getUserId(), userDetails.getUsername(), userDetails.getRole());
        }
        // 로그인 실패
        log.info("[SecurityUtil/getLoginUserInfo] ########### 로그인 실패 ##################");
        return null;
    }


}
