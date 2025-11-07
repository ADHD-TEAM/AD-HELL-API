package com.adhd.ad_hell.domain.user.query.service;

import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.domain.user.query.dto.UserDTO;
import com.adhd.ad_hell.domain.user.query.dto.request.AdminSearchRequest;
import com.adhd.ad_hell.domain.user.query.mapper.AdminMapper;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminQueryServiceImpl implements AdminQueryService {

    private final AdminMapper adminMapper;
    private final UserCommandRepository userCommandRepository;
    @Override
    public List<UserDTO> findAllByUsers(AdminSearchRequest adminSearchRequest) {
        log.info("[AdminQueryController/findAllByUsers] 관리자 - 회원 목록 가져오기");

        List<UserDTO> response = adminMapper.findAllByUsers(adminSearchRequest)
                .stream().toList();

        return response;
    }

    @Override
    @Transactional
    public UserDTO findByUserId(Long userId) {

        User user = userCommandRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND)
                        );

        UserDTO response = UserDTO.builder()
                            .roleType(user.getRoleType().name())
                            .loginId(user.getLoginId())
                            .nickname(user.getNickname())
                            .email(user.getEmail())
                            .status(user.getStatus().name())
//                            .deactivatedAt(user.getDeactivatedAt().toLocalDate())
//                            .deletedAt(user.getDeletedAt().toLocalDate())
//                            .createdAt(user.getCreatedAt().toLocalDate())
//                            .updatedAt(user.getUpdatedAt().toLocalDate())
                            .build();

        return response;
    }
}
