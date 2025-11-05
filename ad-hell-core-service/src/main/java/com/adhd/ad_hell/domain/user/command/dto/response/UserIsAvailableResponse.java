package com.adhd.ad_hell.domain.user.command.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserIsAvailableResponse {
    private Boolean userIsAvailable;



    public static UserIsAvailableResponse toUserIsAvailableResponse(Boolean response) {
        return UserIsAvailableResponse.builder()
                .userIsAvailable(response)
                .build();

    }
}
