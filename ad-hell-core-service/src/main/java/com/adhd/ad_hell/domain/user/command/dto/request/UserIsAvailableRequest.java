package com.adhd.ad_hell.domain.user.command.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserIsAvailableRequest {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min=2, message = "닉네임 최소 길이는 {min}글자입니다.")
    @Size(max=30, message = "닉네임 최대 길이는 {max}글자입니다.")
    private String nickname;
}
