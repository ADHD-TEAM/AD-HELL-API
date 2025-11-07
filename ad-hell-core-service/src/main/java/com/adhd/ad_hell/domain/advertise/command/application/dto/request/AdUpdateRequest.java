package com.adhd.ad_hell.domain.advertise.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdUpdateRequest {
  private Long categoryId;
  private String title;
}
