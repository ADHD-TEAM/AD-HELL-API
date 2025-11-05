package com.adhd.ad_hell.domain.user.command.dto.request;

import com.adhd.ad_hell.domain.user.command.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;


@Getter
@Builder
public class UserSignUpRequest {

    @NotBlank(message="아이디를 입력해주세요.")
    @Size(max=30, message = "아이디 최대 길이는 {max}글자입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min=8, message = "비밀번호 최소 길이는 {min}글자입니다.")
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min=2, message = "닉네임 최소 길이는 {min}글자입니다.")
    @Size(max=30, message = "닉네임 최대 길이는 {max}글자입니다.")
    private String nickname;

    @Email
    @NotBlank(message="이메일를 입력해주세요.")
    private String email;

    private Role role;
}
