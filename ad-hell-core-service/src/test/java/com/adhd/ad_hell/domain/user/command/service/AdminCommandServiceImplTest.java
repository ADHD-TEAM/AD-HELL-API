package com.adhd.ad_hell.domain.user.command.service;

import com.adhd.ad_hell.domain.user.command.dto.request.AdminModifyRequest;
import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.entity.UserStatus;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCommandServiceImplTest {
    private User user;

    @InjectMocks
    private AdminCommandServiceImpl adminCommandService;

    @Mock
    private UserCommandRepository userCommandRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 초기 User 객체를 생성
        user = User.builder()
                .nickname("originalNickname")
                .email("original@example.com")
                .password("originalEncodedPassword")
                .status(UserStatus.ACTIVATE)
                .roleType(Role.USER)
                .build();
    }

    @Test
    @DisplayName("modify - 모든 필드 변경 테스트")
    void modify_allFields() {
        // given
        AdminModifyRequest request = AdminModifyRequest.builder()
                .nickname("newNickname")
                .email("new@example.com")
                .password("newPassword123")
                .status(UserStatus.DEACTIVATE) // 유효한 값으로 수정
                .role(Role.ADMIN)
                .build();

        // passwordEncoder.encode가 호출될 때 반환할 값을 설정
        when(passwordEncoder.encode(request.getPassword())).thenReturn("newEncodedPassword");

        // when
        user.modify(request, passwordEncoder);

        // then
        // 각 필드가 요청된 값으로 변경되었는지 확인
        assertEquals("newNickname", user.getNickname());
        assertEquals("new@example.com", user.getEmail());
        assertEquals(UserStatus.DEACTIVATE, user.getStatus()); // 수정된 값으로 검증
        assertEquals(Role.ADMIN, user.getRoleType());

        // passwordEncoder.encode가 정확히 1번 호출되었는지 검증
        verify(passwordEncoder, times(1)).encode("newPassword123");
        // 비밀번호가 인코딩된 값으로 변경되었는지 확인
        assertEquals("newEncodedPassword", user.getPassword());
    }

    @Test
    @DisplayName("관리자 - 회원 상태 변경 성공 테스트")
    void patchByUserStatus_success() {
        // given
        long userId = 1L;
        UserStatus newStatus = UserStatus.DEACTIVATE;

        // findByUserId로 찾아올 가짜 User 객체 생성
        // User.patchStatus() 메소드 호출을 검증하기 위해 spy로 감싼다.
        User mockUser = spy(User.builder().userId(userId).status(UserStatus.ACTIVATE).build());

        // userCommandRepository가 mockUser를 반환하도록 설정
        when(userCommandRepository.findByUserId(userId)).thenReturn(Optional.of(mockUser));

        // when
        adminCommandService.patchByUserStatus(userId, newStatus);

        // then
        // 1. mockUser의 patchStatus 메소드가 올바른 인자로 호출되었는지 검증
        verify(mockUser, times(1)).patchStatus(newStatus);

        // 2. userCommandRepository.save 메소드가 mockUser 객체로 호출되었는지 검증
        verify(userCommandRepository, times(1)).save(mockUser);
    }
}