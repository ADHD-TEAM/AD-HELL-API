package com.adhd.ad_hell.domain.user.command.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPointResponse {
  private Integer amount;
  private String message;
}
