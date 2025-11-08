package com.adhd.ad_hell.domain.user.query.service;

import com.adhd.ad_hell.domain.user.command.entity.Role;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.entity.UserStatus;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.domain.user.query.dto.UserDTO;
import com.adhd.ad_hell.domain.user.query.dto.request.AdminSearchRequest;
import com.adhd.ad_hell.domain.user.query.mapper.AdminMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminQueryServiceImplTest {

    @InjectMocks
    private AdminQueryServiceImpl adminQueryService;

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private UserCommandRepository userCommandRepository;


    @Test
    @DisplayName("관리자 - 회원 목록 조회 성공 테스트")
    void findAllByUsers_success() {
        // given
        AdminSearchRequest request = new AdminSearchRequest(); // 검색 조건 설정 (필요 시)

        // adminMapper가 반환할 가짜 UserDTO 목록 생성
        List<UserDTO> mockUserList = List.of(
                UserDTO.builder().userId(1L).loginId("user1").nickname("nick1").build(),
                UserDTO.builder().userId(2L).loginId("user2").nickname("nick2").build()
        );

        // adminMapper.findAllByUsers가 호출될 때 mockUserList를 반환하도록 설정
        when(adminMapper.findAllByUsers(request)).thenReturn(mockUserList);

        // when
        List<UserDTO> response = adminQueryService.findAllByUsers(request);

        // then
        // 1. 반환된 목록이 null이 아니고, 크기가 2인지 검증
        assertNotNull(response);
        assertEquals(2, response.size());

        // 2. 반환된 목록의 내용이 mockUserList와 동일한지 검증
        assertEquals(mockUserList, response);

        // 3. adminMapper.findAllByUsers가 올바른 인자로 1번 호출되었는지 검증
        verify(adminMapper, times(1)).findAllByUsers(request);
    }

    @Test
    @DisplayName("ID로 사용자 단건 조회 성공 테스트")
    void findByUserId_success() {
        // given
        long userId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // userCommandRepository가 반환할 가짜 User 엔티티 생성
        User mockUser = User.builder()
                .userId(userId)
                .roleType(Role.USER)
                .loginId("testUser")
                .nickname("testNickname")
                .email("test@example.com")
                .status(UserStatus.ACTIVATE)
                .createdAt(now.minusDays(1))
                .updatedAt(now)
                .deactivatedAt(null)
                .deletedAt(null)
                .build();

        // userCommandRepository.findByUserId가 호출될 때 mockUser를 포함한 Optional을 반환하도록 설정
        when(userCommandRepository.findByUserId(userId)).thenReturn(Optional.of(mockUser));

        // when
        UserDTO response = adminQueryService.findByUserId(userId);

        // then
        // 1. 반환된 DTO가 null이 아닌지 확인
        assertNotNull(response);

        // 2. User 엔티티의 필드들이 UserDTO로 올바르게 변환되었는지 검증
        assertEquals(mockUser.getRoleType().name(), response.getRoleType());
        assertEquals(mockUser.getLoginId(), response.getLoginId());
        assertEquals(mockUser.getNickname(), response.getNickname());
        assertEquals(mockUser.getEmail(), response.getEmail());
        assertEquals(mockUser.getStatus().name(), response.getStatus());
        assertEquals(mockUser.getCreatedAt(), response.getCreatedAt());
        assertEquals(mockUser.getUpdatedAt(), response.getUpdatedAt());
        assertEquals(mockUser.getDeactivatedAt(), response.getDeactivatedAt());
        assertEquals(mockUser.getDeletedAt(), response.getDeletedAt());

        // 3. repository 메소드가 올바른 인자로 1번 호출되었는지 검증
        verify(userCommandRepository, times(1)).findByUserId(userId);
    }
}