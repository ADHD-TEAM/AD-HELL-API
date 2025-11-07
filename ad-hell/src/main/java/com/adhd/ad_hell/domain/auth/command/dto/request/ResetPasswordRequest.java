package com.adhd.ad_hell.domain.auth.command.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResetPasswordRequest {

    private Long userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min=8, message = "비밀번호 최소 길이는 {min}글자입니다.")
    private String password;


}
