package com.adhd.ad_hell.domain.user.command.service;

import com.adhd.ad_hell.common.dto.CustomUserDetails;
import com.adhd.ad_hell.domain.user.command.dto.request.UserModifyRequest;
import com.adhd.ad_hell.domain.user.command.dto.request.UserSignUpRequest;
import com.adhd.ad_hell.domain.user.command.dto.response.UserDetailResponse;
import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.entity.UserStatus;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {


    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    @Mock
    private UserCommandRepository userCommandRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUp_success() {
        // given
        UserSignUpRequest request = UserSignUpRequest.builder()
                .loginId("newUser")
                .password("password123")
                .nickname("newNickname")
                .email("new@example.com")
                .build();

        // 아이디와 닉네임이 중복되지 않는다고 가정
        when(userCommandRepository.existsByloginId(request.getLoginId())).thenReturn(false);
        when(userCommandRepository.existsByNickname(request.getNickname())).thenReturn(false);
        // 비밀번호 인코딩 결과 설정
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        // when
        userCommandService.singUp(request);

        // then
        // userCommandRepository.save()가 호출되었는지 검증하기 위해 ArgumentCaptor 사용
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userCommandRepository, times(1)).save(userCaptor.capture());

        // 저장된 User 객체의 속성 검증
        User savedUser = userCaptor.getValue();
        assertEquals(request.getLoginId(), savedUser.getLoginId());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(request.getNickname(), savedUser.getNickname());
        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals(Role.USER, savedUser.getRoleType());
    }


    @Test
    @DisplayName("마이페이지 회원정보 조회 성공 테스트")
    void getUserDetail_success() {
        // given
        String loginId = "testUser";

        // CustomUserDetails 객체를 Mocking
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(mockUserDetails.getLoginId()).thenReturn(loginId);

        LocalDateTime now = LocalDateTime.now();
        User mockUser = User.builder()
                .loginId(loginId)
                .nickname("testNickname")
                .email("test@example.com")
                .status(UserStatus.ACTIVATE)
                .createdAt(now.minusDays(10))
                .deactivatedAt(null)
                .amount(1000)
                .build();

        // userCommandRepository가 mockUser를 반환하도록 설정
        when(userCommandRepository.findByLoginId(loginId)).thenReturn(Optional.of(mockUser));

        // when
        UserDetailResponse response = userCommandService.getUserDetail(mockUserDetails);

        // then
        // 1. 반환된 응답 객체의 필드들이 mockUser의 값과 일치하는지 검증
        assertNotNull(response);
        assertEquals(mockUser.getLoginId(), response.getLoginId());
        assertEquals(mockUser.getNickname(), response.getNickname());
        assertEquals(mockUser.getEmail(), response.getEmail());
        assertEquals(mockUser.getStatus(), response.getStatus());
        assertEquals(mockUser.getCreatedAt(), response.getCreatedAt());
        assertEquals(mockUser.getDeactivatedAt(), response.getDeactivatedAt());
        assertEquals(mockUser.getAmount(), response.getAmount());

        // 2. repository 메소드가 올바른 인자로 호출되었는지 검증
        verify(userCommandRepository, times(1)).findByLoginId(loginId);
    }

    @Test
    @DisplayName("회원 정보 수정 성공 테스트")
    void modifyByUserInfo_success() {
        // given
        String loginId = "testUser";
        String newNickname = "modifiedNickname";

        // CustomUserDetails 객체 Mocking
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(mockUserDetails.getLoginId()).thenReturn(loginId);

        // UserModifyRequest 객체 생성
        UserModifyRequest request = UserModifyRequest.builder()
                .nickname(newNickname)
                .build();

        // findByLoginId로 찾아올 가짜 User 객체 생성
        // User.modifyByUserInfo() 메소드 호출을 검증하기 위해 spy로 감싼다.
        User mockUser = spy(User.builder().loginId(loginId).nickname("originalNickname").build());

        // userCommandRepository가 mockUser를 반환하도록 설정
        when(userCommandRepository.findByLoginId(loginId)).thenReturn(Optional.of(mockUser));

        // when
        userCommandService.modifyByUserInfo(mockUserDetails, request);

        // then
        // 1. mockUser의 modifyByUserInfo 메소드가 올바른 인자로 호출되었는지 검증
        verify(mockUser, times(1)).modifyByUserInfo(request);

        // 2. userCommandRepository.save 메소드가 mockUser 객체로 호출되었는지 검증
        verify(userCommandRepository, times(1)).save(mockUser);
    }

    @Test
    @DisplayName("회원 탈퇴 성공 테스트")
    void withdrawByUserInfo_success() {
        // given
        String loginId = "testUser";

        // CustomUserDetails 객체 Mocking
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);
        when(mockUserDetails.getLoginId()).thenReturn(loginId);

        // findByLoginId로 찾아올 가짜 User 객체 생성
        // User.patchStatus() 메소드 호출을 검증하기 위해 spy로 감싼다.
        User mockUser = spy(User.builder().loginId(loginId).status(UserStatus.ACTIVATE).build());

        // userCommandRepository가 mockUser를 반환하도록 설정
        when(userCommandRepository.findByLoginId(loginId)).thenReturn(Optional.of(mockUser));

        // when
        userCommandService.withdrawByUserInfo(mockUserDetails);

        // then
        // 1. mockUser의 patchStatus 메소드가 UserStatus.DELETE 인자로 호출되었는지 검증
        verify(mockUser, times(1)).patchStatus(UserStatus.DELETE);

        // 2. userCommandRepository.save 메소드가 mockUser 객체로 호출되었는지 검증
        verify(userCommandRepository, times(1)).save(mockUser);
    }
}