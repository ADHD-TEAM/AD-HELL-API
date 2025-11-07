package com.adhd.ad_hell.domain.user.command.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserPointRequest {
  private final Integer point;
}
