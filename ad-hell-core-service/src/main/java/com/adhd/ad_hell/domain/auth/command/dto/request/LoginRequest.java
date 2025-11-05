package com.adhd.ad_hell.domain.auth.command.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequest {

    @NotBlank(message="아이디를 입력해주세요.")
    @Size(max=30, message = "아이디 최대 길이는 {max}글자입니다.")
    private String userLoginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min=8, message = "비밀번호 최소 길이는 {min}글자입니다.")
    private String password;

}
