package com.adhd.ad_hell.domain.user.command.entity;


import com.adhd.ad_hell.common.BaseTimeEntity;
import com.adhd.ad_hell.domain.auth.command.dto.request.ResetPasswordRequest;
import com.adhd.ad_hell.domain.user.command.dto.request.AdminModifyRequest;
import com.adhd.ad_hell.domain.user.command.dto.request.UserModifyRequest;
import com.adhd.ad_hell.domain.user.command.dto.request.UserSignUpRequest;


import com.adhd.ad_hell.domain.user.query.dto.UserDTO;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@Table(name="user_info")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role roleType;

    @Column(nullable = false, length = 30)
    private String loginId;

   // @Column(nullable = false, columnDefinition = "TEXT")
   @Column(nullable = false, length = 600)
    private String password;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 50)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVATE;

    private LocalDateTime deactivatedAt;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    @Builder.Default
    private Integer amount = 0;

    public User(Role roleType,  String loginId, String password, String nickname, String email) {
        this.roleType = roleType;
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }
    public void modifyByUserInfo(UserModifyRequest userModifyRequest) {
        this.nickname = userModifyRequest.getNickname();
    }

    public void modify(AdminModifyRequest request, PasswordEncoder passwordEncoder) {
        if(StringUtils.hasText(request.getNickname())) {
            this.nickname = request.getNickname();
        }

        if(StringUtils.hasText(request.getEmail())) {
            this.email = request.getEmail();
        }

        if(StringUtils.hasText(request.getPassword())) {
            this.password = passwordEncoder.encode(request.getPassword());

        }
        status = request.getStatus();
        roleType = request.getRole();
    }

    public void patchStatus(UserStatus status) {
        this.status = status;
        // 상태 변경시 시간 기록
        if (status == UserStatus.DEACTIVATE) {
            this.deactivatedAt = LocalDateTime.now();
        } else if (status == UserStatus.DELETE) {
            this.deletedAt = LocalDateTime.now();
        }
    }

    public void patchPassword(ResetPasswordRequest passwordRequest, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(passwordRequest.getPassword());
    }

    public static User formUser(UserDTO userDTO) {
        return User.builder()
                .userId(userDTO.getUserId())
                .loginId(userDTO.getLoginId())
                .nickname(userDTO.getNickname())
                .status(UserStatus.valueOf(userDTO.getStatus()))
                .deletedAt(userDTO.getDeletedAt())
                .build();
    }

    public void earnPoint(Integer point) {
      this.amount += point;
    }

    public void decreasePoint(Integer point) {
      if (amount < point) throw new BusinessException(ErrorCode.POINT_NOT_ENOUGH);
      this.amount -= point;
    }
}
