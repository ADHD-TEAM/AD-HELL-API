package com.adhd.ad_hell.domain.auth.command.service;

import com.adhd.ad_hell.EmailVerificationCode;
import com.adhd.ad_hell.common.dto.LoginUserInfo;
import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.auth.command.dto.request.*;
import com.adhd.ad_hell.domain.auth.command.dto.response.ExistVerificationCodeResponse;
import com.adhd.ad_hell.domain.auth.command.dto.response.FindUserInfoResponse;
import com.adhd.ad_hell.domain.auth.command.dto.response.TokenResponse;
import com.adhd.ad_hell.domain.auth.command.repository.RefreshTokenRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.domain.user.query.dto.UserDTO;
import com.adhd.ad_hell.domain.user.query.mapper.UserMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import com.adhd.ad_hell.jwt.JwtTokenProvider;
import com.adhd.ad_hell.mail.MailService;
import com.adhd.ad_hell.mail.MailType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UserCommandRepository userCommandRepository;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MailService mailService;
    private final AuthRedisService authRedisService;



    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 아이디 검증
        log.debug("[AuthCommandServiceImpl/login]로그인 아이디 검증");
        User user = userCommandRepository.findByLoginId(request.getUserLoginId())
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.INVALID_USERNAME_OR_PASSWORD));

        // 비밀번호 검증
        log.debug("[AuthCommandServiceImpl/login]비밀번호 검증");
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_USERNAME_OR_PASSWORD);
        }

        // 로그인 성공시 token 발금
        log.debug("[AuthCommandServiceImpl/login]로그인 token 발급");
//        String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId(),user.getRoleType());
//        String refreshToken = jwtTokenProvider.createRefreshToken(user.getLoginId(),user.getRoleType());

        // 사용지 확인용 UserId 발급 추가
        String accessToken = jwtTokenProvider.createAccessTokenWithUser(
                user.getLoginId(),
                user.getUserId(),
                user.getRoleType()
        );

        String refreshToken = jwtTokenProvider.createRefreshTokenWithUser(
                user.getLoginId(),
                user.getUserId(),
                user.getRoleType()
        );

        log.info("[AuthCommandServiceImpl/login] accessToken : {}", accessToken);
        log.info("[AuthCommandServiceImpl/login] refreshToken : {}", refreshToken);


        // 로그인시 레디스에 accessToken/ refreshToken 저장
        authRedisService.saveRefreshToken(user.getUserId(), refreshToken);
        log.info("[AuthCommandServiceImpl/login] 레디스 저장 성공 ");

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenResponse tokenReissue(TokenRequest request) {
        log.info("[AuthCommandServiceImpl/tokenReissue] 토큰재발급");

        // 토큰 밸리데이션 체크
        jwtTokenProvider.vaildateToken(request.getRefreshToken());
        log.info("[AuthCommandServiceImpl/tokenReissue] 토큰재발급- token vaildattion check");

        // 로그인하고 있는 user 가져오기
        LoginUserInfo user = SecurityUtil.getLoginUserInfo();

        log.info("[AuthCommandServiceImpl/tokenReissue] 토큰재발급- userId : {}", user);

        // 레디스에 있는 userId(key)로 리프레시 토큰 있는지 확인
        Boolean exist = authRedisService.existRefreshTokenByUserId(user.getUserId());
        if (Boolean.FALSE.equals(exist)) {
            log.info("[AuthCommandServiceImpl/tokenReissue] 토큰재발급-유효한 토큰 없음");
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_TOKEN);
        }

        // 새로운 access/refresh 토큰 재발금
        String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getLoginId(), user.getRole());
        log.info("[AuthCommandServiceImpl/tokenReissue] 토큰재발급-accessToken/refreshToken 발급");

        // 새로운 refresh 토큰 다시 레디스에 저장
        authRedisService.saveRefreshToken(user.getUserId(), refreshToken);
        log.info("[AuthCommandServiceImpl/tokenReissue] 토큰재발급- 새로운 refreshToken redis 저장");

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    @Override
    @Transactional
    public void logout(String request) {
        // userId(key), access, refresh 필요
        LoginUserInfo user = SecurityUtil.getLoginUserInfo();
        log.info("[AuthCommandServiceImpl/logout] 로그아웃 user={}",user);


        // 레디스에 있는 userId(key)로 리프레시 토큰 있는지 확인
        Boolean exist = authRedisService.existRefreshTokenByUserId(user.getUserId());
        if (Boolean.FALSE.equals(exist)) {
            log.info("[AuthCommandServiceImpl/logout] 로그아웃-유효한 토큰 없음");
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_TOKEN);
        }

        // userId(key)로 refresh token 가져오기
         String refreshToken = authRedisService.findKeyByUserId(user.getUserId());
        log.info("[AuthCommandServiceImpl/logout] 로그아웃- refreshToken 가져오기 : {}", refreshToken);

        // 토큰 남은 유효시간
        long remainingTime = jwtTokenProvider.getRemainingTime(refreshToken);
        log.info("[AuthCommandServiceImpl/logout] 로그아웃- 토큰 유효시간 : {}", refreshToken);

        // refresh token 제거
        authRedisService.deleteRefreshTokenByUserId(user.getUserId());
        log.info("[AuthCommandServiceImpl/logout] 로그아웃- 토큰 유효시간 : {}", refreshToken);

        // access 토큰 블랙리스트 처리
        authRedisService.addBlackListAccessToken(request, remainingTime);
        log.info("[AuthCommandServiceImpl/logout] 로그아웃-access 토큰 블랙리스트 처리: {}", refreshToken);

    }


    @Override
    @Transactional
    public void sendVerificationCode(SendEmailVerifyUserRequest request) {
        log.info("[AuthCommandServiceImpl/sendEmail] 본인인증 - 이메일로 인증번호 보내기 |");
        String toEmail = request.getEmail();
        String receiverName = request.getLoginid();
        MailType mailType = MailType.VERIFICATION;

        // 인증번호 생성
        String code = EmailVerificationCode.getCode();
        log.info("[AuthCommandServiceImpl/sendEmail] 본인인증 인증번호 생성 , code {} |", code);

        // 레디스 저장
        authRedisService.saveValidityCode(toEmail, code);
        log.info("[AuthCommandServiceImpl/sendEmail] 레디스 저장 , toEmail, code {} , {} |",toEmail, code);

        // 이메일 발송
        try {
            mailService.sendMail(toEmail, receiverName, mailType, code);
            log.info("[AuthCommandServiceImpl/sendEmail] 이메일 발송 성공 ");
        } catch (Exception e) {
            throw new RuntimeException("메일 전송 실패");
        }

    }

    @Override
    public ExistVerificationCodeResponse existVerificationCode(
            ExistVerificationCodeRequest request) {
        log.info("[AuthCommandServiceImpl/existVerificationCode] 인증번호 있는지 확인");
        // 레디스에서 email로 key값과 value 값이 있는 체크


         Boolean exist = authRedisService.existVerificationCode(
                 request.getEmail(), request.getVerificationCode());

        log.info("[AuthCommandServiceImpl/sendEmail] 인증번호 exist={}", exist);

        // 레디스에 있는 값 삭제
        authRedisService.deleteKeyEmail(request.getEmail());

        return ExistVerificationCodeResponse.builder()
                .exist(exist)
                .build();
    }

    @Override
    public FindUserInfoResponse getUserInfo(String email, String loginId) {
        log.info("[AuthCommandServiceImpl/getUserInfo] 아이디/비밀번호 찾기 ");

        // user
        UserDTO user = userMapper.findByEmailAndLoginId(email, loginId);
        log.info("[AuthCommandServiceImpl/getUserInfo] 아이디/비밀번호 찾기 성공 ");

        // 레디스에 있는 값 삭제
        authRedisService.deleteKeyEmail(email);

        return FindUserInfoResponse.builder()
                .userId(user.getUserId())
                .status(user.getStatus())
                .loginId(user.getLoginId())
                .deactivatedAt(user.getDeactivatedAt())
                .email(user.getEmail())
                .build();

    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        log.info("[AuthCommandServiceImpl/resetPassword] 비밀번호 재설정 ");

        // user
        User user = userCommandRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        log.info("[AuthCommandServiceImpl/resetPassword] 비밀번호 재설정 - 유저정보 확인");


        // 비밀번호 변경
        user.patchPassword(request, passwordEncoder);
        userCommandRepository.save(user);
        log.info("[AuthCommandServiceImpl/resetPassword] 비밀번호 재설정 성공 ");
    }



}
