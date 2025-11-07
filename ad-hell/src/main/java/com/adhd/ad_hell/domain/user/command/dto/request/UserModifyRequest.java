package com.adhd.ad_hell.domain.user.command.dto.request;


import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
public class UserModifyRequest {

    private String nickname;

}
