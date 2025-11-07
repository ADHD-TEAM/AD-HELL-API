package com.adhd.ad_hell.domain.auth.command.service;


import com.adhd.ad_hell.EmailVerificationCode;
import com.adhd.ad_hell.common.dto.LoginUserInfo;
import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.auth.command.dto.request.LoginRequest;
import com.adhd.ad_hell.domain.auth.command.dto.request.ResetPasswordRequest;
import com.adhd.ad_hell.domain.auth.command.dto.request.SendEmailVerifyUserRequest;
import com.adhd.ad_hell.domain.auth.command.dto.response.FindUserInfoResponse;
import com.adhd.ad_hell.domain.auth.command.dto.response.TokenResponse;
import com.adhd.ad_hell.domain.user.command.dto.request.UserSignUpRequest;
import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.entity.UserStatus;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.domain.user.command.service.UserCommandServiceImpl;
import com.adhd.ad_hell.domain.user.query.dto.UserDTO;
import com.adhd.ad_hell.domain.user.query.mapper.UserMapper;
import com.adhd.ad_hell.jwt.JwtTokenProvider;
import com.adhd.ad_hell.mail.MailService;
import com.adhd.ad_hell.mail.MailType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthCommandServiceImplTest {

    @InjectMocks
    private AuthCommandServiceImpl authCommandService;

    @Mock
    private AuthRedisService authRedisService;

    @Mock
    private MailService mailService;

    @Mock
    private UserCommandRepository userCommandRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("인증 코드 이메일 발송 성공 테스트")
    void sendVerificationCode_success() {
        // given
        SendEmailVerifyUserRequest request = SendEmailVerifyUserRequest.builder()
                .loginid("testUser")
                .email("test@example.com")
                .build();

        String expectedCode = "FIXED-VERIFICATION-CODE";

        // try-with-resources 구문을 사용하여 정적 Mock을 관리합니다.
        try (MockedStatic<EmailVerificationCode> mockedStatic = Mockito.mockStatic(EmailVerificationCode.class)) {
            // EmailVerificationCode.getCode()가 호출되면 미리 정해둔 코드를 반환하도록 설정
            mockedStatic.when(EmailVerificationCode::getCode).thenReturn(expectedCode);

            // when
            authCommandService.sendVerificationCode(request);

            // then
            // 1. Redis에 인증 코드가 올바르게 저장되었는지 검증
            verify(authRedisService, times(1)).saveValidityCode(request.getEmail(), expectedCode);

            // 2. 메일 발송 메소드가 올바른 인자들로 호출되었는지 검증
            verify(mailService, times(1)).sendMail(
                    request.getEmail(),
                    request.getLoginid(),
                    MailType.VERIFICATION,
                    expectedCode
            );
        }
    }


    @Test
    @DisplayName("로그인 성공 테스트")
    void login_success() {
        // given
        LoginRequest request = LoginRequest.builder()
                .userLoginId("testUser")
                .password("password123")
                .build();

        User mockUser = User.builder()
                .userId(1L)
                .loginId("testUser")
                .password("encodedPassword")
                .roleType(Role.USER)
                .status(UserStatus.ACTIVATE)
                .build();

        // Mock 객체들의 동작 정의
        when(userCommandRepository.findByLoginId(request.getUserLoginId())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createAccessToken(mockUser.getLoginId(), mockUser.getRoleType())).thenReturn("new-access-token");
        when(jwtTokenProvider.createRefreshToken(mockUser.getLoginId(), mockUser.getRoleType())).thenReturn("new-refresh-token");

        // when
        TokenResponse response = authCommandService.login(request);

        // then
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());

        // authRedisService.saveRefreshToken이 올바른 인자들로 호출되었는지 검증
        verify(authRedisService, times(1)).saveRefreshToken(mockUser.getUserId(), "new-refresh-token");
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logout_success() {
        // given
        String accessToken = "dummy-access-token";
        String refreshToken = "dummy-refresh-token";
        long userId = 1L;
        long remainingTime = 3600000L; // 1시간

        LoginUserInfo mockUser = new LoginUserInfo(userId, "testUser", Role.USER);

        // SecurityUtil.getLoginUserInfo() 정적 메소드 Mocking
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getLoginUserInfo).thenReturn(mockUser);

            // Mock 객체들의 동작 정의
            when(authRedisService.existRefreshTokenByUserId(userId)).thenReturn(true);
            when(authRedisService.findKeyByUserId(userId)).thenReturn(refreshToken);
            when(jwtTokenProvider.getRemainingTime(refreshToken)).thenReturn(remainingTime);

            // when
            authCommandService.logout(accessToken);

            // then
            // 1. Refresh Token이 삭제되었는지 검증
            verify(authRedisService, times(1)).deleteRefreshTokenByUserId(userId);

            // 2. Access Token이 블랙리스트에 추가되었는지 검증
            verify(authRedisService, times(1)).addBlackListAccessToken(accessToken, remainingTime);
        }
    }

    @Test
    @DisplayName("사용자 정보 조회 성공 테스트")
    void getUserInfo_success() {
        // given
        String email = "test@example.com";
        String loginId = "testUser";
        LocalDateTime deactivatedAt = LocalDateTime.now().plusYears(1);

        UserDTO mockUserDTO = UserDTO.builder()
                .userId(1L)
                .loginId(loginId)
                .email(email)
                .status("ACTIVE")
                .deactivatedAt(deactivatedAt)
                .build();

        // userMapper가 mockUserDTO를 반환하도록 설정
        when(userMapper.findByEmailAndLoginId(email, loginId)).thenReturn(mockUserDTO);

        // when
        FindUserInfoResponse response = authCommandService.getUserInfo(email, loginId);

        // then
        // 1. 반환된 응답 객체 검증
        assertNotNull(response);
        assertEquals(mockUserDTO.getUserId(), response.getUserId());
        assertEquals(mockUserDTO.getLoginId(), response.getLoginId());
        assertEquals(mockUserDTO.getEmail(), response.getEmail());
        assertEquals(mockUserDTO.getStatus(), response.getStatus());
        assertEquals(mockUserDTO.getDeactivatedAt(), response.getDeactivatedAt());

        // 2. userMapper 메소드가 올바른 인자로 호출되었는지 검증
        verify(userMapper, times(1)).findByEmailAndLoginId(email, loginId);

        // 3. Redis 키 삭제 메소드가 올바른 인자로 호출되었는지 검증
        verify(authRedisService, times(1)).deleteKeyEmail(email);
    }

    @Test
    @DisplayName("비밀번호 재설정 성공 테스트")
    void resetPassword_success() {
        // given
        long userId = 1L;
        String newPassword = "newPassword123";
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .userId(userId)
                .password(newPassword)
                .build();

        // findByUserId로 찾아올 가짜 User 객체 생성
        // patchPassword 메소드를 테스트하기 위해 실제 User 객체를 스파이(spy)로 사용
        User mockUser = spy(User.builder().password("oldEncodedPassword").build());

        // userCommandRepository가 mockUser를 반환하도록 설정
        when(userCommandRepository.findByUserId(userId)).thenReturn(Optional.of(mockUser));

        // when
        authCommandService.resetPassword(request);

        // then
        // 1. mockUser의 patchPassword 메소드가 올바른 인자들로 호출되었는지 검증
        verify(mockUser, times(1)).patchPassword(request, passwordEncoder);

        // 2. userCommandRepository.save 메소드가 호출되었는지,
        //    그리고 그 인자가 우리가 수정한 mockUser와 동일한지 검증
        verify(userCommandRepository, times(1)).save(mockUser);
    }



}