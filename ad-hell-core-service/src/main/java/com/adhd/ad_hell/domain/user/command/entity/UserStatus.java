package com.adhd.ad_hell.domain.user.command.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

      ACTIVATE("ACTIVATE", "활성화 상태")
    , DEACTIVATE("DEACTIVATE", "비활성화 상태")
    , DELETE( "DELETE" , "탈퇴 상태");


    private String type;
    private String typeName;

}
